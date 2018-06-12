package frol;

import com.google.common.collect.Lists;
import org.jbpm.services.api.model.DeployedAsset;
import org.kie.internal.runtime.conf.RuntimeStrategy;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DeployedUnitDto implements Serializable {
    private final String identifier;
    private final RuntimeStrategy runtimeStrategy;
    private final List<String> deployedClasses;
    private final List<DeployedAsset> deployedAssets;
    private final boolean active;

    public DeployedUnitDto(String identifier, RuntimeStrategy runtimeStrategy, Collection<Class<?>> deployedClasses,
                           Collection<DeployedAsset> deployedAssets, boolean active) {
        this.identifier = identifier;
        this.runtimeStrategy = runtimeStrategy;
        this.deployedClasses = deployedClasses.stream().map(Class::getName).collect(Collectors.toList());
        this.active = active;
        this.deployedAssets = Lists.newArrayList(deployedAssets);
    }

    public String getIdentifier() {
        return identifier;
    }

    public RuntimeStrategy getRuntimeStrategy() {
        return runtimeStrategy;
    }

    public List<String> getDeployedClasses() {
        return deployedClasses;
    }

    public List<DeployedAsset> getDeployedAssets() {
        return deployedAssets;
    }

    public boolean isActive() {
        return active;
    }
}
