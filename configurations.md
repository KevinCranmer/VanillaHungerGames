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

**chests.use_vanilla_items**
- `true` if you want the world generated chests to use the default vanilla chest generation. `false` if you want to override what can be in the chest.
- Default: `true`

**chests.custom_item_spawns**
- This is a list of items you want to be randomly appearing in chests if you've set `chests.use_vanilla_items` to `false`.
- Each item has 4 pieces:
    - `name`: [String] The name of the item's material. Must match an Enum Constant from this list: [Material.html](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html)
    - `chance`: [Double] The chance that an item shows up in a chest. Can be anywhere between 0.0 and 1.0.
    - `min`: [Integer] If the item appears in the chest, what's the lower limit for how many of this item should appear?
    - `min`: [Integer] If the item appears in the chest, what's the upper limit for how many of this item should appear?
- Default: empty
- Example: 
```
chests:
    custom_item_spawns:
        - name: LEATHER_HELMET
          chance: 0.8
          min: 1
          max: 2
        - name: LEATHER
          chance: 0.5
          min: 4
          max: 12
        - name: DIAMOND_SWORD
          chance: 0.05
          min: 1
          max: 1
```
