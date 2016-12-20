package com.cep.lib.service;

import akka.actor.ActorSystem;
import akka.cluster.sharding.ClusterShardingSettings;
import com.cep.lib.domain.ClusterShardsConfig;
import com.cep.lib.domain.CepAkkaConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Abstracts AKKA configuration, setup and initialization.
 */

@Component("cepAkkaManager")
public class CepAkkaManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CepAkkaManager.class);

    @Autowired
    private ClusterShardsConfig clusterShardsConfig;

    @Autowired
    private CepAkkaConfig cepAkkaConfig;

    @Autowired
    private ShardRegionInitializer shardRegionInitializer;

    private ActorSystem cepActorSystem;

    private ClusterShardingSettings clusterShardingSettings;


    @PostConstruct
    public void postConstruct() {
        LOGGER.debug("cepActorSystemName: {}", cepAkkaConfig.getCepAkkaConfigFileName());
        cepActorSystem = ActorSystem.create(cepAkkaConfig.getCepActorSystemName(), createConfig());

        clusterShardingSettings = ClusterShardingSettings.create(cepActorSystem);
        shardRegionInitializer.initialize(clusterShardsConfig, cepActorSystem, clusterShardingSettings);
        LOGGER.debug("Created actor system: {}", cepActorSystem);
    }


    private Config createConfig() {

        Config config = ConfigFactory.empty();

        if (cepAkkaConfig.getCepAkkaConfigFileName().length() > 0) {
            LOGGER.info("Use the provided cep.akka.config.file.name = {}", cepAkkaConfig.getCepAkkaConfigFileName());
            config = config.withFallback(ConfigFactory.load(cepAkkaConfig.getCepAkkaConfigFileName()));
        }

        LOGGER.info("Cep akka config {}", config.root().render(ConfigRenderOptions.defaults().setJson(true)));

        return config;
    }

    public String getCepActorSystemName() {
        return cepAkkaConfig.getCepActorSystemName();
    }

    public ActorSystem getCepActorSystem() {
        return cepActorSystem;
    }

    public ClusterShardingSettings getClusterShardingSettings() {
        return clusterShardingSettings;
    }


}
