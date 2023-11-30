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

**allow_spectate_teleporting**
- Whether or not players can teleport to other players while spectating.
- You may want to disable this if players are on teams and they can let their teammates know where an enemy is.
- Default: `true`

**require_admin_to_create**
- If true then only admins can execute `/hgcreate`.
- Default: `true`

**pre_game_lobby.use**
- Whether or not players will sit in a lobby world while waiting for the hunger games to start.
- Set this to true if you don't want players to be able to scout the world ahead of time.
- Default: `false`

**pre_game_lobby.world_name**
- Specify your Hunger Games lobby world name here. Only relevant if `pre_game_lobby.use` is `true`.
- If this field is its default value while `pre_game_lobby.use` is `true`, a simple bedrock box world will be used as the lobby.
- Default: `hg_default_world`

**min_players_to_start**
- The number of players that must join the Hunger Games before the Pre-Game Countdown begins.
- Value must be above 0
- Default: `1`

**pre_game_countdown.minutes**
- Together with `pre_game_countdown.seconds`, this represents the amount of time between when `min_players_to_start` have joined the Hunger Games and when the Hunger Games actually starts.
- Value must be above 0
- Default: `2`

**pre_game_countdown.seconds**
- Together with `pre_game_countdown.minutes`, this represents the amount of time between when `min_players_to_start` have joined the Hunger Games and when the Hunger Games actually starts.
- Value must be between 0 and 59
- Default: `0`
