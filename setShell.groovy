/*
  Change default /bin/sh
*/

import hudson.tasks.*
import jenkins.model.*

def instance = Jenkins.getInstance()
def desc = instance.getDescriptor(hudson.tasks.Shell)

desc.setShell('/bin/bash')
desc.save()
