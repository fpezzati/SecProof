package edu.pezzati.sec.util;

public class FSysPolicyRepo {

    /**
     * Thread t = new Thread() {
     * 
     * @Override public void run() { try (WatchService watcher =
     *           policyStore.getFileSystem().newWatchService();) {
     *           policyStore.register(watcher,
     *           StandardWatchEventKinds.ENTRY_CREATE,
     *           StandardWatchEventKinds.ENTRY_MODIFY,
     *           StandardWatchEventKinds.ENTRY_DELETE); while (true) { WatchKey
     *           key = null; try { key = watcher.take(); } catch
     *           (InterruptedException e) { return; } for (WatchEvent<?> event :
     *           key.pollEvents()) { if (event.kind() ==
     *           StandardWatchEventKinds.ENTRY_CREATE) { AbstractPolicy policy =
     *           getPolicy(new File(policyStore.toFile(), ((Path)
     *           event.context()).toString())); getPolicies().put(((Path)
     *           event.context()).toUri(), policy); } else if (event.kind() ==
     *           StandardWatchEventKinds.ENTRY_DELETE) {
     *           getPolicies().remove(((Path) event.context()).toString()); }
     *           else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
     *           { AbstractPolicy policy = getPolicy(new
     *           File(policyStore.toFile(), ((Path)
     *           event.context()).toString())); getPolicies().put(((Path)
     *           event.context()).toUri(), policy); } if (!key.reset()) { break;
     *           } } } } catch (IOException e) { e.printStackTrace(); } catch
     *           (Exception e) { e.printStackTrace(); } } }; t.start();
     */

}
