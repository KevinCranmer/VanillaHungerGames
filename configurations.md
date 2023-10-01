## VanillaHungerGames Configurations

These configurations are found in `vanilla_hunger_games.yml`

**commands_to_run_after_match**
- List of commands to be ran after a match finishes. You can use this to give a 
reward for the winning player. The variable `{WINNER_NAME}` will be replaced with the
winner's name when the command is executed.
- These execute AFTER the hunger games world has been REMOVED. So there may be a little delay for 
users if they leave the match immediately after a winner is decided.
- NOTE: Do not include a `/` before the command.
- Syntax should look like this:
```
commands_to_run_after_match:
 - give {WINNER_NAME} diamond 1
 - eco give {WINNER_NAME} 1000
```
- Default: Empty list
