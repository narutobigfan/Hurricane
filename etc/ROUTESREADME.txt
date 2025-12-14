Step 1: Setup a Discord bot (you will need to host it and get the Discord token).

Step 2: Copy the Discord token and paste it into the Discord token field in Integration settings.
You don't need to adjust the Discord API URL unless the version number has been updated.

Step 3: Acquire the Discord Channel ID you wish Discord alerts to be posted in. You will need Discord Dev Controls enabled.

Step 4: Open Settings, then Custom Settings, then Routes, then you can input your account name (your username you logged into).

Step 4: Input the character name you're logging into.

Step 5: Enter the message you want to be posted in the Discord. The message will also contain the character name and the waypoint it found the thing at.

Step 6: Enter the Discord Channel ID you acquired in Step 3.

Step 7: Enter the target name which will be the exact resource name of what you're spotting. For example, if it's a cave angler
it'd be "gfx/kritter/caveangler/caveangler" without the quotation marks. If you're unsure of the res name you can hover over it with CTRL+Shift.

Step 8: Enter the route name, you will use this regularly to load/save your route.

Step 9: Enter your relog time, this is in minutes.

Step 10: Choose if you want the character to hearth back after it completes the path. If you don't hearth it should either wait or log out at the end of the path.
When the relog time is up it will then reverse the path.

Step 11: Choose if you want it to pick up things, this would be for if your target name is a forageable.

Step 12: Setup all the waypoints for the path you want the character to follow, add a new waypoint every time you reach the end of your click.

Step 13: After all of this you can press "Save Route." It should save this route with the name you gave it into your data folder.

Step 14: From here you could press "Add route" and then you could press "Run" and it should start working down the path you gave it.

If you first login the way you load a specific route is by typing the route name in the route name section and then pressing load.
Every time you load a route you'd need to press "Add Route" again, then "Run" to run it.

You can also run multiple routes at once if you load a saved route, press add route, then load another saved route, and press add route, etc.
It will queue all the routes you've added.

A good example in which this is useful would be if you had an account with 3 characters on 3 different angler rivers. It would login the first one
then go through to the end of the river reporting if it finds any, then login to the 2nd char, then continue from there.

A few notes quick notes: It would be best to make your waypoints a bit smaller if you're hunting around aggressive animals.
When it spots a cave angler for example, it will spot it, then finish it's current waypoint click, then it will report it in Discord, then it will turn around and boat towards the starting path and logout.

Also if you've queued multiple routes at once the login time would be applied at the end of the last route.
So if it takes you 20 minutes to complete your route then you may not need a very long relog time for the area to have reloaded. 

