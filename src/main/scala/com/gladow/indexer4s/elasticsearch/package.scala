package com.gladow.indexer4s

import com.sksamuel.elastic4s.indexes.CreateIndexDefinition

package object elasticsearch {

  implicit class EnhancedCreateIndexDef(definition: CreateIndexDefinition) {
    def shards(optShards: Option[Int]): CreateIndexDefinition = optShards
      .fold(definition)(s => definition shards s)
    def replicas(optReplicas: Option[Int]): CreateIndexDefinition = optReplicas
      .fold(definition)(s => definition replicas s)
  }
}