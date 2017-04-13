/*

Can be used for:
  * install plugins
  * update plugins

*/

import jenkins.model.*
import hudson.model.*

String[] pluginsArray = ['put your plugins in this array!']
String[] installedPlugins = Jenkins.instance.pluginManager.plugins

def instance = Jenkins.getInstance()

pluginsArray.each{
   if (installedPlugins.contains("Plugin:${it}") == false) { Jenkins.instance.updateCenter.getPlugin(it).deploy() } else { println "Plugin ${it} already installed" }
}

//instance.restart()
