package com.eg.assignment.server.model

import com.spotify.docker.client.DefaultDockerClient
import com.whisk.docker.DockerCommandExecutor

case class DockerManager(dockerClient: DefaultDockerClient, dockerCommandExecutor: DockerCommandExecutor)
