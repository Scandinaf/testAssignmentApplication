package com.eg.assignment.server.exception

/*
This wrapper allows you to separate internal and external exceptions, which should be processed correctly by the system
 */
case class InternalError(ex: Exception) extends Exception(ex)
