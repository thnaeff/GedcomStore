GedcomStore
==========
To parse lineage-linked grammar files and build the GEDCOM-structure according to the parsed definitions

<p>
GedcomStore is a library to parse lineage-linked grammar from a file. It then gives access to the parsed structures in order to create a valid GEDCOM ouput. Using the GedcomCreator class makes it even easier to write and access values in the structure.
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



Examples
---------
Some examples can be found at src/ch/thn/gedcom/store/test/




Not yet implemented
--------------------
<ul>
<li>Value possibilities (often appearing as [Y|&lt;NULL&gt;])</li>
</ul>


