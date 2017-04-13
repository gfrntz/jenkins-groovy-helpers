/*

Use Plugin ID.

pluginsForUpdate example:

String[] pluginsForUpdate = ["ansicolor", "ccm"]

*/

String[] pluginsForUpdate = []

if( pluginsForUpdate.size() > 0) {
  pluginsForUpdate.each{
    Jenkins.instance.updateCenter.getPlugin(it).deploy()
  }
} else{
  Jenkins.instance.updateCenter.getUpdates().each{
    it.deploy()
  }
}
