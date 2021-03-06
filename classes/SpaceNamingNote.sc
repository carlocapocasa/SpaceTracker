/*
    SpaceTracker for SuperCollider 
    Copyright (c) 2013 - 2017 Carlo Capocasa. All rights reserved.
    https://capocasa.net

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
*/


SpaceNamingNote {
  var
    mods,
    tones,
    octaves,
    <length=4
  ;
  classvar
    scale
  ;

  *initClass {
    // Note: Can still use this for
    // any western scale, just like you
    // can play a piano in any key. Just using
    // "major from c" as an easy way to get semitones
    // Still leaving it changable for experimentation
    // purposes
    scale = Scale.major;
  }

  *new {
    ^super.new.init;
  }

  init {
    mods = TwoWayIdentityDictionary[
      $b -> -1,
      $x ->  1,
      $c -> -2,
      $y ->  2,
    ];

    tones = TwoWayIdentityDictionary[
      $c -> 0,
      $d -> 1,
      $e -> 2,
      $f -> 3,
      $g -> 4,
      $a -> 5,
      $b -> 6
    ];

  }

  string {
    arg note;
    var octave, tone, mod, semi;
    if (note == 0) {
      ^0;
    };
    semi = scale.semitones;
    tone = (note % 12).asFloat;
    octave = ((note - tone)/12).asInteger - 2;
    mod = if(semi.indexOf(tone).isNil,1,0);
    tone = (tone-mod).asFloat;
    tone = semi.indexOf(tone);
    tone= tones.getID(tone);
    
    mod=mods.getID(mod)?"";

    ^tone++octave++mod;
  }

  number {
    arg note;
    var found, octave, tone, mod;
    if (note ? 0 == 0) {
      ^0;
    };
    note = note.asString.toLower;
    found = note.findRegexp("^[a-g](-?[0-9]+)[bxcy]?$");
    if (found.size > 0) {
      tone = tones.at(note[0]);
      tone = scale.at(tone);
      octave = found[1][1].asInteger;
      octave = octave + 2;
      mod = mods.at(note[note.size-1]) ? 0;
      ^12 * octave + tone + mod;
    } {
      ^nil;
    };
  }
}

