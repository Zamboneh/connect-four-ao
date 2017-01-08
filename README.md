# connect-four-ao

##Description
This program is designed for use by the Atomic Object Connect Four game board as part of Atomic Games events. It takes a board and a player, and uses an alpha-beta pruning minimax algorithm with a transposition table to output the best move as an integer representing which column to drop a piece into.

The AI itself is always running in the background. The launch.bat script calls the Connect class, which uses a socket connection to relay the board and player information to the AI. This is necessary for the transposition table's operation, as the table would be empty on every turn if the program had to launch fresh every time.

##Operation
Run the Main class. Then use launch.bat as the player for the game board.

##Retro
My end goal was to use an MTD(f) algorithm to hone in on a best move, but once I finally got the scoring and alpha-beta working correctly, I could not come up with a good way to integrate this algorithm without rewriting a whole slew of it. Given more time I would most assuredly make a better attempt to tackle this.

Apart from that, the real hiccups encountered during development mostly stemmed from debugging the alpha-beta side of the algorithm. For quite some time I couldn't figure out why the AI would skip out on winning moves if more than one existed ("Eh, I already won the game, so let's just do column 0!"). This was solved by adding a search depth check - if a move resulted in a win on a much sooner turn, take that one.

All in all this was a great learning experience. I found it a great refresher on minimax, and I also learned about transposition tables, Zobrist hashing, etc. for the first time. Good stuff.

##Resources
####MTD(f)
http://people.csail.mit.edu/plaat/mtdf.html
####Minimax and alpha-beta pruning, scoring
https://www.gimu.org/connect-four-js/
https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning
####Transposition tables
https://en.wikipedia.org/wiki/Transposition_table
https://web.archive.org/web/20070822204120/www.seanet.com/~brucemo/topics/hashing.htm
https://web.archive.org/web/20070822204038/http://www.seanet.com/~brucemo/topics/zobrist.htm
