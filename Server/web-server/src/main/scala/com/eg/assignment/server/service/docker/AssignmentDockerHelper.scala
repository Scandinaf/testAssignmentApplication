package com.eg.assignment.server.service.docker

import java.nio.file.Path

import com.whisk.docker.VolumeMapping

trait AssignmentDockerHelper {
  protected val containerPath: Path

  def buildEnvVariable(envName: String, envValue: String): String =
    s"$envName=$envValue"

  def buildVolumeMapping(basePath: Path): VolumeMapping =
    VolumeMapping.apply(
      basePath.toString,
      containerPath.toString,
      true
    )
}
