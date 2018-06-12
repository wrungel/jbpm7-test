package frol;

import com.google.common.collect.Lists;
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

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Path("/deploymentService")
@Stateless
public class DeploymentRestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentRestService.class);

    private static final String ASSETS_PATH = "/assets/";
    @Inject
    DeploymentService deploymentService;

    @Context
    private UriInfo uriInfo;
    @Context
    private HttpHeaders httpHeaders;


    static final String GROUP_ID = "de.soptim.suite";
    static final String ARTIFACT_ID = "frol";
    static final String VERSION = "1.0";

    @GET
    @Path("/deployedUnits")
    @Produces({ "application/json" })
    public List<DeployedUnitDto> deployedUnits() {
        return deployedUnitsInternal();
    }

    /**
     * <a href="https://stackoverflow.com/questions/5738665/how-to-get-source-file-in-httprequest-for-uploading-file">https://stackoverflow.com/questions/5738665/how-to-get-source-file-in-httprequest-for-uploading-file</a>
     */
    @PUT
    @Path("/deploy/{groupId}/{artifactId}/{version}")
    @Consumes({"application/zip"})
    public Response deployZip(@PathParam("groupId") String groupId,
                              @PathParam("artifactId") String artifactId,
                              @PathParam("version") String version,
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
        LOGGER.info("Deployed " + deploymentUnit.getIdentifier() + ", strategy: " + deploymentUnit.getStrategy().name());
        return Response.created(uriInfo.getAbsolutePath()).build();
    }

    @PUT
    @Path("/deploy/{groupId}/{artifactId}/{version}/{assetName}")
    @Consumes({"text/plain"})
    public Response deploy(@PathParam("groupId") String groupId,
                           @PathParam("artifactId") String artifactId,
                           @PathParam("version") String version,
                           @PathParam("assetName") String assetName,
                           byte[] contents) throws IOException {


        DeploymentUnit deploymentUnit = deployBasicKieJar(groupId, artifactId, version, Lists.newArrayList(new AssetDto(assetName, contents)));
        LOGGER.info("Deployed " + deploymentUnit.getIdentifier() + ", strategy: " + deploymentUnit.getStrategy().name());
        return Response.created(uriInfo.getAbsolutePath()).build();
    }


    public DeploymentUnit deployBasicKieJar(String groupId, String artifactId, String version, List<AssetDto> assets) throws IOException {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(groupId, artifactId, version);

        deployKieJar(ks, releaseId, assets);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(groupId, artifactId, version);
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

    private List<DeployedUnitDto> deployedUnitsInternal() {
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
                LOGGER.error("Error Message: ({}) {}", message.getPath(), message.getText());
            }
            throw new RuntimeException("There are errors building the package, please check your knowledge assets!");
        }
        return (InternalKieModule) kieBuilder.getKieModule();
    }

    private static KieFileSystem createKieFileSystemWithKProject(KieServices ks) {
        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase-test").setDefault(true).addPackage("*")
                .setEqualsBehavior(EqualityBehaviorOption.EQUALITY).setEventProcessingMode(EventProcessingOption.STREAM);
        KieSessionModel kieSessionModel1 = kieBaseModel1.newKieSessionModel("ksession-test").setDefault(true).setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType(ClockTypeOption.get("realtime"));
        kieSessionModel1.newWorkItemHandlerModel("Log", "new frol.SystemOutWorkItemHandler()");
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        return kfs;
    }


}