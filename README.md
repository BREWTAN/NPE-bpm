##NPE
 New Process Engine.<br>


##What is NPE
 
 
 Based on scala/AKKA framework, NPE is work for simple flows control. NPE can handle bpmn v2.0 files, including FSM and MCS modules.<br>
 
##FSM
  
  
 FSM means flow state machine, which receive new process or submit for activity, and then produce new activity to priority queues. FSM can use mysql or ehcache as its backend. The mysql backend is using mauricio/postgresql-async(https://github.com/mauricio/postgresql-async/).   <br>
  
  
##MCS
  
  
 MCS means monitor and control system, which handles submit and monitor from restful interfaces. MCS is base on PLAY framework, and talk to FSM with akka remote API and as cluster member. MCS also using ACE bootstrap framework.<br><br>


##Tech

 NPE includes AKKA, scala, bootstrap, asyncdb, play framework,queue etc.
<br>
