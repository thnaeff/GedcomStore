GedcomStore
==========
<b>To parse a lineage-linked grammar file and build a valid GEDCOM-structure according to the parsed definitions</b>

<p>
GedcomStore is a library to parse lineage-linked grammar from a file. It then gives access to the parsed structures in order to create a valid GEDCOM ouput. Using the GedcomCreator class makes it even easier to create multiple structures like family or individual structures.<br>
Since the structures are created according to the grammar file, the output does not have to be validated - only valid ouput can be created.
</p>

Key features
-------------
<ul>
<li>Create your own gedcom definition file using lineage-linked grammar (preferably, take one of the "standards" and modify them to your needs if necessary)</li>
<li>Comes with the gedcom definition files for versio 5.5 and 5.5.1, plus a slightly modified version for the GRAMPS software</li>
<li>Choose if you want structures to be automatically populated with all mandatory lines, with all lines or no lines</li>
<li>Easy creation and access to the structures, lines and values
	<ul>
	<li>Just define the "path" to the needed line in an array (or variable argument string), like: "INDI", "REFN", "TYPE", ...</li>
	<li>It automatically follows the given path and creates any missing structures or lines</li>
	<li>If a path is invalid and can not be followed, it tries to give hints on what the next path item could possibly be</li>
	</ul>
</li>
<li>The GedcomPrinter class to create the output
	<ul>
	<li>Choose if empty lines (with no value) should be printed or not</li>
	<li>Pick any block/line to start printing from</li>
	<li>Choose the maximum level of the output</li>
	<li>Choose if linked structures should be included in the ouput or not</li>
	<li>Print the whole or parts of the parsed lineage-linked grammar file</li>
	</ul>
</li>
<li>The GedcomCreator class
	<ul>
	<li>Holds a map of structures of a given type</li>
	<li>The structures can be accessed through their ID</li>
	<li>A helper class to create all the individuals/families/... when creating a GEDCOM file</li>
	</ul>
</li>
</ul>



A short instruction
---------------------
<p>
At first, a GedcomStore is needed and the lineage-linked grammar file has to be parsed:
<code><pre>
GedcomStore store = new GedcomStore();
store.parse("PATH_TO_FILE/gedcomobjects_5.5.1.txt");
</pre></code>
Watch the command line output since it might give information about parts which can not be pased etc. The method <code>showParsingOutput()</code> can be used to show/hide output about all the parsed lines.<br>
<br>
At this point, all the structures are accessible through the GedcomStore. The first step is to get a starting block of a certain structure from the store. A block contains one or more lines, and each line has it's own block (if there are any child-lines available).<br>
</p>

<br>
<p>
The following code line gets a INDIVIDUAL_RECORD. There are three modes available: MANDATORY, ALL and NONE. Each of them defines how the structure is initially created. MANDATORY for example creates a structure with all the mandatory lines already added.
<pre><code>
GedcomBlock block = store.getGedcomBlock("INDIVIDUAL_RECORD", GedcomBlock.ADD_MANDATORY);
</code></pre>
</p>

<br>
<p>
Now, with the structure as block, you can navigate throught the structure in two ways. Either step by step by using <code>getChildLine(), getParentLine(), getParentBlock(), addStructure(), addTagLine()</code> etc. or simply by using the method <code>followPath()</code> which also takes care of any missing lines.<br>
The following lines show the usage of those two ways.
<code><pre>
GedcomLine line = block.getChildLine("INDI").getBlock().addStructureLine("PERSONAL_NAME_STRUCTURE").getChildLine("NAME");
GedcomObject object = block.followPath("INDI", "PERSONAL_NAME_STRUCTURE", "NAME");
</pre></code>
The first line shows how to follow the structure step by step (and adding the missing part PERSONAL_NAME_STRUCTURE to the block of a line). 
The second line shows how to use the more convenient way with <code>followPath()</code>, where missing parts are created automatically. 
The second way is easier to use, but also slower (it always has to follow the whole path). However, a combination of the first and the second way is possible too of course, since each GedcomLine or GedcomBlock is a subclass of GedcomObject, which implements the followPath method.<br>
<i>Hint: When accessing/creating lines, supporting output is given on the command line showing the accessed and added lines. It can be turned off with <code>store.showAccessOutput(false)</code></i>
</p>

<br>
<p>
If a GedcomLine (or GedcomTagLine) is returned (which is a subclass of GedcomObject), values can be set and retreived. As defined in the lineage-linked grammar, a line can (but must not) have a value and/or a xref. The value can be set with setValue, the xref with setXRef (the getter methods are getValue and getXRef of course).
<code><pre>
line.setValue("some value");
</pre></code>
If a GedcomObject is returned, it could be cast to a GedcomTagLine (if it is an instance of course), or the more convenient way below can be used which does the cast for you
<code><pre>
object.getTagLine().setValue("some other value");
</pre></code>
</p>

<br>
<p>
Now that we have created a little structure with some lines, it is time to print it. The class GedcomPrinter helps with that.<br>
Here are two possibilities to print a line itself and a line with it's child-lines. Using the examples does not produce a very exciting output since the returned line does not have any child lines.
<code><pre>
System.out.println(GedcomPrinter.preparePrint(line));
System.out.println(GedcomPrinter.preparePrint(line, 2, false, true));
</pre></code>
or a whole block...
<code><pre>
System.out.println(GedcomPrinter.preparePrint(object.getParentBlock(), 0, true, true));
</pre></code>
or how about the whole store, limited to level 2...
<code><pre>
System.out.println(GedcomPrinter.preparePrint(store, 2, false));
</pre></code>
</p>


Examples
---------
<p>
More examples can be found in src/ch/thn/gedcom/store/test/
</p>



Not yet implemented
--------------------
<ul>
<li>Value possibilities (often appearing as [Y|&lt;NULL&gt;])</li>
</ul>


