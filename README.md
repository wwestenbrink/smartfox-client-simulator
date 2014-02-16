Smartfox client simulator
====

The goal of this project is to simulate smartfox clients connecting & interaction with a smartfox server.

Every client is launched on its own thread, status and some timings of clients can be viewed in a table and all clients log to a text area.

This initial release uses some hard coded values to let you launch 50 SfsTestClients that connect & login to your smartfox server listening on localhost.

Modify / Build / Run
=====
For now you can open the project using IntelliJ to modify, build & run the project.

build an executable .jar file using "build artifacts" to distribute.

Clients
======

SfsWarmupClient - used for warming up the simulation by connect->login->disconnect

SfsTestClient - simple test client, connect->login

Modify or add your own client to simulate more complex interactions.