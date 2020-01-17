package com.eg

import cats.Eq

package object assignment {
  implicit val vertexEq: Eq[Vertex] = Eq.fromUniversalEquals
  implicit val edgeEq: Eq[Edge] = Eq.fromUniversalEquals
}
