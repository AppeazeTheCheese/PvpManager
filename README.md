# PvpManager
<u><b>UNFINISHED</b></u>

This plugin allows players to toggle PVP on and off. This will prevent them from damaging and being damaged by other players via melee, projectiles, potions (poison and instant damage), and TNT. *This does NOT prevent mob or self damage*. Players will have a 10 second cooldown after engaging in pvp, during which they cannot use `/pvp off`. Admins may force the PVP state of other players by passing the player name to either `/pvp on` or `/pvp off`.

<h1>Commands</h1>

<ul>
  <li>
    /pvp on [player]
      <ul><li>Turns on PVP for yourself or the target player if specified</li></ul>
  </li>
  <li>
    /pvp off [player]
      <ul><li>Turns off PVP for yourself or the target player if specified</li></ul>
  </li>
  <li>
    /pvp toggle
      <ul><li>Toggles PVP for yourself</li></ul>
  </li>
</ul>

<h1>Permissions</h1>
<ul>
  <li>
    pvpmanager.toggle
      <ul><li>Allows the user to toggle their own PVP state. Enabled by default.</ul></li>
  </li>
  <li>
    pvpmanager.toggleothers
      <ul><li>Allows the user to toggle the PVP state of other players by passing the player's name to the PVP on or PVP off command. Disabled by default.</ul></li>
  </li>
  <li>
    pvpmanager.ignorecooldown
      <ul><li>Allows the user to bypass the 10 second cooldown after engaging in PVP. Disabled by default.</ul></li>
  </li>
</ul>
