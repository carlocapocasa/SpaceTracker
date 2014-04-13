
SpaceRead {
  var
    tree,
    sounds,
    liner,
    totaltimes,
    numChannels,
  ;

  initClass {
    treeClass = SpaceTree;
    linerClass = SpaceLiner;
    tmpClass = SpaceTmp;
  }

  *new {
    arg tree, sounds, liner;
    
    ^super.newCopyArgs(tree, sounds, liner).init;
  }

  init {
    tree.parse({
      arg line;
      line = liner.convertToNumeric(line);
      numChannels = line.size;
      if (numChannels >1, \break, nil); // break only if not a pause
    });
  }

  read {
    var index, time, times, indentTime, indentTimes;
    
    index = 0;
    time = 0;
    indentTime = 0;
    times = Array.fill(polyphony, 0);
    indentTimes = List.new.add(0);
    
    tree.parse({
      arg line, indent, lastIndent;

      // [indent, lastIndent,((lastIndent - indent).abs * 0.5).round,indentTimes].postln;
      
      block {
        arg continue;
        if (line.isNil) {
          continue.();
        };
        if (indent % 2 == 1, {
          
          // Odd indent does parallelization, so we figure out
          // which channel to use
          
          // Keep track of indentTime by indent level
          // No note of a higher indent can come sooner than this
          if (indent > lastIndent) {
            var num;
            indentTime = times.maxItem;
            ((indent - lastIndent) * 0.5).round.asInteger.do({
              indentTimes.add(indentTime);
            });
          };
          
          index = times.minIndex;
          time = times[index];
          if (time > indentTime, {
            (this.class.name + "dropped note" + line).postln;
            continue.();
          });
        });

        if (indent % 2 == 0, {
          if (indent < lastIndent) {
            ((lastIndent - indent) * 0.5).round.asInteger.do({
              indentTimes.pop;
            });
            indentTime = indentTimes.last;
          };
        });
        
        //// Good, we figured out which channel we can use from
        //// indentation. Now insert the note.

        // Insert pre-pause if necessary
        // Parallel, so relative to indentTime when parallel started
        // Fill up with pause
        if (times[index] < indentTime) {
          sounds[index].writeData(FloatArray.fill(numChannels, 0).put(0, indentTime-times[index]));
          times[index] = indentTime;
        };
        // Insert main line
        line = this.convertToNumeric(line);
        sounds[index].writeData(line);
        times[index] = times[index] + line[0];
        
        // Must keep this debug line!
        // [index,line,times].postln;
      };
    });

    sounds.do({
      arg sound;
      sound.close;
    });
  }
}

