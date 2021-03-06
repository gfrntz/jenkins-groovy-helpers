/*
  Script from https://github.com/batmat/jez/blob/master/jenkins-master/init_scripts/configure_docker_swarm_cloud.groovy
*/

import jenkins.model.*
import hudson.model.*

import com.cloudbees.plugins.credentials.Credentials
import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import com.cloudbees.plugins.credentials.SystemCredentialsProvider

import com.nirima.jenkins.plugins.docker.*
import com.nirima.jenkins.plugins.docker.launcher.*
import com.nirima.jenkins.plugins.docker.strategy.*

def dockerCertificatesDirectory = System.getenv('DOCKER_CERTIFICATES_DIRECTORY')

def dockerCertificatesDirectoryCredentialsId = 'docker-certificates-credentials'
def jenkinsSlaveCredentialsId = 'jenkins-ssh-slave-credentials'

///////////////////////////////////////////////////:
// Configure credz
///////////////////////////////////////////////////:
def system_creds = SystemCredentialsProvider.getInstance()

Map<Domain, List<Credentials>> domainCredentialsMap = system_creds.getDomainCredentialsMap()

domainCredentialsMap[Domain.global()].add(

  new UsernamePasswordCredentialsImpl(
    CredentialsScope.SYSTEM,
    jenkinsSlaveCredentialsId,
    'Jenkins slave docker container credentials.',
    'jenkins',
    'jenkins'
    )
)

domainCredentialsMap[Domain.global()].add(

    new com.nirima.jenkins.plugins.docker.utils.DockerDirectoryCredentials(
      CredentialsScope.SYSTEM,
      dockerCertificatesDirectoryCredentialsId,
      'Contains the certificates required to authenticate against a Docker TLS secured port',
      dockerCertificatesDirectory
    )
)

system_creds.save()
println 'Added docker cloud credentials.'

/////////////////////////////////////////////////////:
// Docker Cloud config per-se
/////////////////////////////////////////////////////:
def dockerRegistryUrl = "registry.hub.docker.com"
assert dockerRegistryUrl != null : "dockerRegistryUrl env var not set!"

def docker_settings = [:]
docker_settings =
[
    [
        name: 'docker registry',
        serverUrl: dockerRegistryUrl,
        containerCapStr: '100',
        connectionTimeout: 5,
        readTimeout: 15,
        credentialsId: dockerCertificatesDirectoryCredentialsId,
        version: '',
        templates: [
            [
                image: 'ubuntu:14.04',
                labelString: 'ubuntu',
                remoteFs: '/home/jenkins',
                credentialsId: jenkinsSlaveCredentialsId,
                idleTerminationMinutes: '5',
                sshLaunchTimeoutMinutes: '1',
                jvmOptions: '',
                javaPath: '',
                memoryLimit: 500,
                memorySwap: 0,
                cpuShares: 0,
                prefixStartSlaveCmd: '',
                suffixStartSlaveCmd: '',
                instanceCapStr: '3',
                dnsString: '',
                dockerCommand: '',
                volumesString: '',
                volumesFromString: '',
                hostname: '',
                bindPorts: '22',
                bindAllPorts: false,
                privileged: false,
                tty: false,
                macAddress: ''
            ]
        ]
    ]
]

def dockerClouds = []
docker_settings.each { cloud ->

  def templates = []
  cloud.templates.each { template ->
      def dockerTemplateBase =
          new DockerTemplateBase(
             template.image,
             template.dnsString,
             template.dockerCommand,
             template.volumesString,
             template.volumesFromString,
             template.environmentsString,
             template.lxcConfString,
             template.hostname,
             template.memoryLimit,
             template.memorySwap,
             template.cpuShares,
             template.bindPorts,
             template.bindAllPorts,
             template.privileged,
             template.tty,
             template.macAddress
      )

      def dockerTemplate =
        new DockerTemplate(
          dockerTemplateBase,
          template.labelString,
          template.remoteFs,
          template.remoteFsMapping,
          template.instanceCapStr
        )

      def dockerComputerSSHLauncher = new DockerComputerSSHLauncher(
          new hudson.plugins.sshslaves.SSHConnector(22, template.credentialsId, null, null, null, null, null )
      )

      dockerTemplate.setLauncher(dockerComputerSSHLauncher)

      dockerTemplate.setMode(Node.Mode.NORMAL)
      dockerTemplate.setNumExecutors(1)
      dockerTemplate.setRemoveVolumes(true)
      dockerTemplate.setRetentionStrategy(new DockerOnceRetentionStrategy(10))
      dockerTemplate.setPullStrategy(DockerImagePullStrategy.PULL_LATEST)

      templates.add(dockerTemplate)
  }

  dockerClouds.add(
    new DockerCloud(cloud.name,
                    templates,
                    cloud.serverUrl,
                    cloud.containerCapStr,
                    cloud.connectTimeout ?: 15, // Well, it's one for the money...
                    cloud.readTimeout ?: 15,    // Two for the show
                    cloud.credentialsId,
                    cloud.version
    )
  )
}

if(!Jenkins.instance.clouds.getByName('Docker registry')) {
  Jenkins.instance.clouds.addAll(dockerClouds)
  println 'Configured docker cloud.'
}
