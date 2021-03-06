package com.yannick_cw.elastic_indexer4s.elasticsearch.index_ops

import scala.concurrent.{ExecutionContext, Future}
import cats.implicits._
import com.yannick_cw.elastic_indexer4s.Index_results.{IndexError, StageSucceeded, StageSuccess}

class IndexDeletion(esClient: EsOpsClientApi)(implicit ec: ExecutionContext) {

  import esClient._

  def deleteOldest(indexPrefix: String,
                   newIndex: String,
                   keep: Int,
                   protectAlias: Boolean): Future[Either[IndexError, StageSucceeded]] =
    (for {
      allIndices <- allIndicesWithAliasInfo
      toDelete = allIndices
        .filter(_.index.startsWith(indexPrefix))
        .sortBy(_.creationTime)
        .filterNot(_.index == newIndex)
        .dropRight(keep)
        .filter(info => if (protectAlias) info.aliases.isEmpty else true)
        .map(_.index)
      _ <- toDelete.traverse(delete)
    } yield StageSuccess(s"Deleted indices: ${toDelete.mkString("[", ",", "]")}")).value
}

object IndexDeletion {
  def apply(esClient: EsOpsClientApi)(implicit ec: ExecutionContext): IndexDeletion = new IndexDeletion(esClient)
}
