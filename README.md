RpEngine2
=========

A rewrite of the Bukkit plugin RPEngine. Fully modular and configurable.

## Contents

* [New Features](#new-features)
    * [Card](#fully-dynamic--configurable-card)
        * [Attribute System](#attribute-system)
        * [Selects](#selects)
        * [Special Attributes](#special-attributes)
    * [Inspect](#inspect--action-menu)
    * [Countdown](#countdown)
    * [Duels](#duels)
    * [Combat Log](#combat-log)
    * [Nametag Support](#nametag-support)
    * [Chat Channels](#chat-channels)
* [Commands](#commands)
    * [Core Commands](#core-commands)
    * [Chat Commands](#chat-commands)
    * [Duel Commands](#duel-commands)

## New Features

### Fully Dynamic / Configurable `/card`

The core of this plugin contains a new "attribute" system to allow you to configure `/card` however
you like. You are no longer limited by the hardcoded values of the previous version's card and can
add or remove new attributes as you please.

![card](https://i.imgur.com/LC5SXUI.png)

Functionally, to an end user, `/card` functions pretty much the same as it did before.

#### Attribute System

You can add new attributes to the card with 

```
/rpengine attribute add <name> [default_value] [display_name]
/rpengine attribute addnum <name> [default_value] [display_name]
```

> a.k.a. `/rpengine at ...`

In addition to `strings` and `numbers`, you can create a third attribute type called a `select`.

#### Selects

Selects allow you to define an attribute that a user chooses a value from a pre-determined set of 
options. For example, you might have a `select` called `Caste` with the options `Serf`, `Acolyte`, 
`Brother`.

![select](https://i.imgur.com/Wvxgclx.png)

For example, you can create the pictured select and add it as an attribute with:

```
/rpengine select Caste
/rpengine select addopt Caste Serf gray
/rpengine select addopt Caste Brother blue
/rpengine select addopt Caste Acolyte green
/rpengine at addselect Caste Serf
```

> a.k.a `/rpengine sel ...`

#### Special Attributes

There are also a few "special" attributes that you can assign after creating an attribute:
* `/rpengine at setidentity <name>` - Sets the attribute as the main display name for the user throughout the plugin
* `/rpengine at setmarker <name>` - Select attributes only, uses the color of the select option as a prefix in chat
* `/rpengine at settitle <name>` - Sets the attribute as the "title" used in chat

Notes:
* `addnum` will require user input to be a number
* You can set the `format` of a `number` with `/rpengine at setformat ...` (e.g. if you want no decimal places use `/rpengine at setformat <name> %.0f`)

### Inspect / Action Menu

Clicking on a player's name in chat will pull up an "action menu" to perform player related actions
such as sending a bird to them or inspecting their player card. Inspect also has a configurable 
block range players must be within to inspect each other.

![action menu](https://i.imgur.com/VGk75jI.png)

You can inspect other players cards with the button in the action menu or with

```
/inspect <player>
```

### Countdown

![countdown](https://i.imgur.com/4OmJZpd.gif)

The `/countdown [seconds]` command will broadcast a countdown to players within a configurable 
block range. The colors of the text as well as the sound that played on each number are 
configurable as well.

### Duels

[Preview](https://i.imgur.com/w0ruDAg.mp4)

Players can duel each other (without the penalty of death or loss of items) with the `/duel` 
command. More information about how duels work can be viewed with `/duelrules`

### Combat Log

[Preview](https://i.imgur.com/SXO5qF1.mp4)

Additionally, this plugin adds a new feature to display damage / regen above entities heads when
regenerating health or if damaged by another player.

This feature depends on the (optional) `Holograms` plugin

### Nametag Support

Displaying a player's identity on their nametag is also supported with the use of (optional) 
`ProtocolLib` and the `NametagEdit` plugins.

To have a user's `identity` be displayed on their nametag, simply install `ProtocolLib`. To have the
color of their nametag match their `marker` attribute, install `NametagEdit`.

Both dependencies are completely optional and are not co-dependant on each other.

### Chat Channels

Also, new with this rewrite, is a fully configurable and dynamic chat channel system. Through the
config, you can configure as many chat channel as you would like. The legacy `rp` and `ooc` chat
runs through this new system; however, aliases have been defined to support the continued use of 
commands like:

* `/rp`
* `/ooc`
* `/toggleooc`

Additionally, the `shout` and `whisper` commands also run through chat channels now. Functionally,
they behave identically. 

For each channel you can configure:

* `range` - Block range where players can hear you when sending a message in this channel
* `prefix` - String to insert to beginning of each message in this channel
* `messageColor` - The color of the actual message content
* `permission` - The permission a user is required to have to send or receive messages in the channel. If this is unset, all users will be permitted to use the channel

## Commands

All commands in RpEngine2 can be enabled/disabled through the configuration. Additionally, every
command can be assigned a cooldown.

### Core Commands

| Name | Description | Permission | Usage | Aliases |
| ---- | ----------- | ---------- | ----- | ------- |
| rpengine | Admin command for RPEngine2 | `rpengine.admin` | `/rpengine help` |
| card | Display RP character card | `rpengine.card` | `/card` |
| cardset | Sets a card attribute | `rpengine.cardset` | `/cardset help` |
| cardselect | Lists options for a select attribute | `rpengine.cardselect` | `/cardselect help` |
| inspect | Allows you to view other players cards | `rpengine.inspect` | `/inspect <player>` |
| colorlist | Lists available colors | `rpengine.colorlist` | `/colorlist` |
| playeractionmenu | Displays the player action menu (internal) | `rpengine.playeractionmenu` | `/playeractionmenu <player>` |
| countdown | Starts a countdown for nearby players | `rpengine.countdown` | `/countdown [seconds]` |
| roll | Generates a random number | `rpengine.roll` | `/roll [max \| min [max]]` | `rand,rnd`
| played | Prints play time on server | `rpengine.played` | `/played` |

### Chat Commands

| Name | Description | Permission | Usage | Aliases |
| ---- | ----------- | ---------- | ----- | ------- |
| channel | Channel management command (join, mute, etc.) | `rpengine.chat.channel` | `/channel help` |
| whisper | Speak softly and carry a big stick | `rpengine.chat.whisper` | `/whisper <message>` | `w` |
| shout | Et tu brute | `rpengine.chat.shout` | `/shout <message>` | `yell` |
| bird | A delayed message determined by the distance between players | `rpengine.chat.bird` | `/bird <player> <message>` |
| ooc | Alias for /channel join ooc (legacy) | `rpengine.chat.ooc` | `/ooc` |
| rp | Alias for /channel join rp (legacy) | `rpengine.chat.rp` | `/rp` |
| toggleooc | Alias for /channel mute ooc (legacy) | `rpengine.chat.toggleooc` | `/toggleooc` |
| emote | You feel as though... | `rpengine.chat.emote` | `/emote <phrase>` | `em,me` |

### Duel Commands

| Name | Description | Permission | Usage | Aliases |
| ---- | ----------- | ---------- | ----- | ------- |
| duelrules | Prints the duel rules | `rpengine.duels.duelrules` | `/duelrules` | `drules` |
| duel | Challenges a player to duel or accepts an active challenge | `rpengine.duels.duel` | `/duel <player>` |
| dueldecline | Declines a duel challenge | `rpengine.duels.dueldecline` | `/dueldecline <player>` | `ddecline` |
| forfeit | Forfeits an active duel | `rpengine.duels.forfeit` | `/forfeit` | `yield,concede` |
