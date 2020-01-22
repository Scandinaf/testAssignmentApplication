package com.eg.assignment.server.dao.db

import java.util.concurrent.ConcurrentHashMap

import com.couchbase.client.scala.{AsyncBucket, AsyncCluster, Cluster}
import com.couchbase.client.scala.implicits.Codec

import scala.concurrent.ExecutionContext

trait CouchbaseDao {
  protected def cluster: AsyncCluster
  protected def bucket: AsyncBucket
}

object CouchbaseDao {
  val clusters: ConcurrentHashMap[String, Cluster] = new ConcurrentHashMap()
}
