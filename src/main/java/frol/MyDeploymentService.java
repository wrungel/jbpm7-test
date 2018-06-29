package frol;

import static org.kie.internal.runtime.conf.RuntimeStrategy.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.appformer.maven.integration.embedder.MavenSettings;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class MyDeploymentService {

    private static final Logger LOG = LoggerFactory.getLogger(MyDeploymentService.class);

    @Inject
    private DeploymentService deploymentService;


    public DeploymentUnit deploy(String groupId,
                           String artifactId,
                           String version,
                           String assetName,
                           byte[] contents) throws IOException {


        DeploymentUnit deploymentUnit = deployBasicKieJar(groupId, artifactId, version, Lists.newArrayList(new AssetDto(assetName, contents)));
        LOG.info("Deployed {}, strategy: {}", deploymentUnit.getIdentifier(), deploymentUnit.getStrategy());
        return deploymentUnit;
    }

    DeploymentUnit deployZip(String groupId,
                              String artifactId,
                              String version,
                              byte[] zipContents) throws IOException {

        List<AssetDto> assets = new ArrayList<>();
        try (ByteArrayInputStream in = new ByteArrayInputStream(zipContents); ZipInputStream zis = new ZipInputStream(in)) {
            ZipEntry ze;

            while ((ze = zis.getNextEntry()) != null) {
                String resourcePath = ze.getName();
                int len;
                byte[] buff = new byte[1024];
                ByteArrayOutputStream out = new ByteArrayOutputStream(ze.getSize() > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) ze.getSize());
                while ((len = zis.read(buff)) > 0) {
                    out.write(buff, 0, len);
                }
                assets.add(new AssetDto(resourcePath, out.toByteArray()));
            }
        }
        DeploymentUnit deploymentUnit = deployBasicKieJar(groupId, artifactId, version, assets);
        LOG.info("Deployed {}, strategy: {}", deploymentUnit.getIdentifier(), deploymentUnit.getStrategy());;
        return deploymentUnit;
    }

    List<DeployedUnitDto> deployedUnitsInternal() {
        Collection<DeployedUnit> deployedUnits = deploymentService.getDeployedUnits();
        return deployedUnits.stream().map(
                u -> new DeployedUnitDto(
                        u.getDeploymentUnit().getIdentifier(),
                        u.getDeploymentUnit().getStrategy(),
                        u.getDeployedClasses(),
                        u.getDeployedAssets(),
                        u.isActive()))
                .collect(Collectors.toList());
    }


    private DeploymentUnit deployBasicKieJar(String groupId, String artifactId, String version, List<AssetDto> assets) throws IOException {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(groupId, artifactId, version);

        deployKieJar(ks, releaseId, assets);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(groupId, artifactId, version, null, null,
                PER_PROCESS_INSTANCE.name());
        deploymentService.deploy(deploymentUnit);
        return deploymentUnit;
    }

    private void deployKieJar(KieServices ks, ReleaseId releaseId, List<AssetDto> assets) throws IOException {
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdirs();
        try (FileOutputStream fs = new FileOutputStream(pom)) {
            fs.write(getPom(releaseId).getBytes());
        }


        InternalKieModule kJar1 = createKieJar(ks, releaseId, assets);

        KieMavenRepository repository = KieMavenRepository.getKieMavenRepository();
        System.out.println( "Local repo is: " + MavenSettings.getSettings().getLocalRepository() );
        repository.installArtifact(releaseId, kJar1, pom);
    }

    private static String getPom(ReleaseId releaseId, ReleaseId... dependencies) {
        String pom = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + " xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n"
                + " <modelVersion>4.0.0</modelVersion>\n" + "\n" + " <groupId>" + releaseId.getGroupId() + "</groupId>\n" + " <artifactId>"
                + releaseId.getArtifactId() + "</artifactId>\n" + " <version>" + releaseId.getVersion() + "</version>\n" + "\n";
        if (dependencies != null && dependencies.length > 0) {
            pom += "<dependencies>\n";
            for (ReleaseId dep : dependencies) {
                pom += "<dependency>\n";
                pom += " <groupId>" + dep.getGroupId() + "</groupId>\n";
                pom += " <artifactId>" + dep.getArtifactId() + "</artifactId>\n";
                pom += " <version>" + dep.getVersion() + "</version>\n";
                pom += "</dependency>\n";
            }
            pom += "</dependencies>\n";
        }
        pom += "</project>";
        return pom;
    }

    private static InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, List<AssetDto> assets) throws IOException {
        KieFileSystem kfs = createKieFileSystemWithKProject(ks);
        kfs.writePomXML(getPom(releaseId));

        for (AssetDto asset : assets) {
            kfs.write("src/main/resources/KBase-test/" + asset.getPath(), asset.getContent());
        }

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        if (!kieBuilder.buildAll().getResults().getMessages().isEmpty()) {
            for (Message message : kieBuilder.buildAll().getResults().getMessages()) {
                LOG.error("Error Message: ({}) {}", message.getPath(), message.getText());
            }
            throw new RuntimeException("There are errors building the package, please check your knowledge assets!");
        }
        return (InternalKieModule) kieBuilder.getKieModule();
    }

    private static KieFileSystem createKieFileSystemWithKProject(KieServices ks) {
        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel = kproj.newKieBaseModel("kbase-test").setDefault(true).addPackage("*")
                .setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
                .setEventProcessingMode(EventProcessingOption.STREAM)
                .setScope(RequestScoped.class.getName());
        KieSessionModel kieSessionModel1 = kieBaseModel.newKieSessionModel("ksession-test")
                .setDefault(true)
                .setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType(ClockTypeOption.get("realtime"));
//        kieSessionModel1.newWorkItemHandlerModel("Task1", "new frol.WorkItemHandler1()");
//        kieSessionModel1.newWorkItemHandlerModel("Task2", "new frol.WorkItemHandler2()");
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        return kfs;
    }

}
