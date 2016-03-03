MobileVotingSystem

Should be fully functional.

Admin Commands:
  init
  start
  add proj1,proj2,proj3
  end
  
notes:
  init establishes user as admin
  start initializes tally table and allows voting to occur
  add assigns a number to each project starting from 1. This number is what users input when voting
  end displays winners

User Commands:
  <number> - corresponds with which project the user wants to vote on

Text message acknowledgements are sent with each transaction.
In addition, a display log is provided on the application.

TODO:
  Disable voting after end message is sent
  Should be some command to prevent additional participants from being added?
  Visual uphaul of application (at least a Pitt icon in the corner?)
