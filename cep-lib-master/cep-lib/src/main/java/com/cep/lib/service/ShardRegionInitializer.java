package com.cep.lib.service;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import com.cep.lib.domain.ClusterShardsConfig;
import com.cep.lib.domain.MessageExtractor;
import com.cep.lib.domain.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Different shard region for different persistence actors must be initialized using this initializer.
 * Initializes based on the configuration provided in the cep.clusterShards section of yaml file.
 */
@Component
public class ShardRegionInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShardRegionInitializer.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MessageExtractor messageExtractor;


    public void initialize(ClusterShardsConfig clusterShardsConfig, ActorSystem actorSystem, ClusterShardingSettings clusterShardingSettings) {

        LOGGER.info("ClusterShardsConfig config {}", clusterShardsConfig);

        for (ClusterShardsConfig.ClusterShard clusterShard : clusterShardsConfig.getClusterShardList()) {

            Parameters parameters = Parameters.instance();

            for (String paramKey : clusterShard.getParameters().keySet()) {
                parameters = parameters.add(paramKey, clusterShard.getParameters().get(paramKey));
            }

            LOGGER.info("Configure cluster shard for {} ", clusterShard);

            ClusterSharding.get(actorSystem).start(clusterShard.getShardRegionName(),
                    Props.create(clusterShard.shardClazz(), applicationContext, parameters),
                    clusterShardingSettings, messageExtractor);
        }
    }
}
