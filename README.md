NPE
 New Process Engine.

   Based on scala/AKKA framework, NPE is work for simple flows control. NPE can handle bpmn v2.0 files, including FSM and MCS modules.
   FSM means flow state machine, which receive new process or submit for activity, and then produce new activity to priority queues. FSM can use mysql or ehcache as its backend. The mysql backend is using mauricio/postgresql-async. 
   MCS means monitor and control system, which can handle submit from restful interface. MCS is base on PLAY framework, and talk to FSM with akka remote API and as cluster member. MCS also using ACE bootstrap framework.
   so NPE includes AKKA, scala, bootstrap etc.
