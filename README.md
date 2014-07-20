# GedcomStore
**A library to load the GEDCOM structure definitions (from a [lineage-linked grammar file](http://homepages.rootsweb.ancestry.com/~pmcbride/gedcom/55gcch2.htm)) into memory and build a valid [GEDCOM](http://en.wikipedia.org/wiki/GEDCOM)-structure according to the parsed definitions**

GedcomStore is a library written in Java to parse lineage-linked grammar from a text file. The lineage-linked grammar defines the structure of a GEDCOM. Through GedcomStore, those structures can then be retrieved as Java objects in order to create a valid GEDCOM ouput.
Since the structures are created according to the grammar file, the output does not have to be validated against the GEDCOM structure - only valid ouput which matches the given grammar file can be created. However, the values itself are not validated automatically but validators can be implemented (see "Not yet implemented").

Download the library [Version 0.2 as .jar file here](GedcomStore_0.2.jar).


## Key features
* Create your own gedcom definition file using lineage-linked grammar (preferably, take one of the "standards" and modify them to your needs if necessary - see the next point)
* Comes with the gedcom definition files for version 5.5 (GedcomNodes_5.5.gedg) and 5.5.1 (GedcomNodes_5.5.1.gedg), plus a slightly modified version for the [GRAMPS](http://gramps-project.org) software (GedcomNodes_5.5.1_gramps.gedg)
* Supports ALL the fields in the GEDCOM lineage-linked grammar (See "Not yet implemented" for details)
* Choose if you want to populate structures with nothing, all mandatory lines or with all available lines
* If lines do not exist in structures when accessed by path, various possibilities are available to create those lines
* Easy creation and access to the structures, lines and values
	* Just define the "path" to the needed line by using an array (or variable argument string), like: `"INDI", "REFN", "TYPE", ...`
	* It automatically follows the given path and creates any missing structures or lines if needed or requested
	* If a path is invalid and can not be followed, it tries to give hints on what the next path item could possibly be
	* For speed optimization, "paths" can be followed step by step
	* A path can be automatically created if it does not exist, a new path branch can be automatically created if the path already exists or the whole path can be created automatically
* Simple output generation with various printer classes
	* `GedcomStorePrinter`: To print the content of the `GedcomStore`, with various options like the structure depth or the inclusion/exclusion of structures
	* `GedcomStructureTreePrinter`: Prints a tree of the whole gedcom structure. It has an option to even print invisible tree nodes and is very useful for debugging when creating a structure
	* `GedcomStructureTextPrinter`: Prints a gedcom structure as text. The output of this printer can be saved in a text file and imported in any software which supports the GEDCOM format
	* `GedcomStructureHTMLPrinter`: Prints the gedcom structure formatted with HTML. Save this output in a HTML file to view the structure in a web browser
	* A `GedcomNode` is an extension of the `TreeNode` in my Util library, thus any `TreePrinter` can be used for printing and new printers can be created by extending that class.


********************************************************************************************************

## From the lineage-linked grammar to a GEDCOM structure

A lineage-linked grammar block could look like the following INDIVIDUAL_RECORD 
as defined in gedcom version 5.5.1:
```
INDIVIDUAL_RECORD:=
n @<XREF:INDI>@ INDI    {1:1}
  +1 RESN <RESTRICTION_NOTICE>    {0:1}
  +1 <<PERSONAL_NAME_STRUCTURE>>    {0:M}
  +1 SEX <SEX_VALUE>    {0:1}
  +1 <<INDIVIDUAL_EVENT_STRUCTURE>>    {0:M}
  +1 <<INDIVIDUAL_ATTRIBUTE_STRUCTURE>>    {0:M}
  +1 <<LDS_INDIVIDUAL_ORDINANCE>>    {0:M}
  +1 <<CHILD_TO_FAMILY_LINK>>    {0:M}
  +1 <<SPOUSE_TO_FAMILY_LINK>>    {0:M}
  +1 SUBM @<XREF:SUBM>@    {0:M}
  +1 <<ASSOCIATION_STRUCTURE>>    {0:M}
  +1 ALIA @<XREF:INDI>@    {0:M}
  +1 ANCI @<XREF:SUBM>@    {0:M}
  +1 DESI @<XREF:SUBM>@    {0:M}
  +1 RFN <PERMANENT_RECORD_FILE_NUMBER>    {0:1}
  +1 AFN <ANCESTRAL_FILE_NUMBER>    {0:1}
  +1 REFN <USER_REFERENCE_NUMBER>    {0:M}
    +2 TYPE <USER_REFERENCE_TYPE>    {0:1}
  +1 RIN <AUTOMATED_RECORD_ID>    {0:1}
  +1 <<CHANGE_DATE>>    {0:1}
  +1 <<NOTE_STRUCTURE>>    {0:M}
  +1 <<SOURCE_CITATION>>    {0:M}
  +1 <<MULTIMEDIA_LINK>>    {0:M}
```

Such a block, stored in a *.gedg file, can be parsed with `GedcomStore`. Instances of the structure can then be retrieved as `GedcomTree` and lines can be added and their values can be set. Those nodes can then be printed using any of the included printer classes.
Such a printer output can look like the following:
```
0 @I1@ INDI
  1 NAME John, Paul /Doe/
    2 GIVN John, Paul
    2 SURN Doe
  1 SEX M
  1 BIRT Y
    2 DATE 10 JAN 1853
  1 FAMS @F1@
  1 CHAN
    2 DATE 13 MAR 2009
      3 TIME 12:13:50
```

This output has a valid GEDCOM format and can be imported into software which supports the GEDCOM format.


********************************************************************************************************


## A short introduction
At first, an instance of `GedcomStore` is needed and the lineage-linked grammar file has to be parsed:

```java
GedcomStore store = new GedcomStore();
try{
	store.parse("PATH_TO_FILE/gedcomobjects_5.5.1.gedg");
} catch (GedcomParseException e) {
	e.printStackTrace();
}
```

If some parts can not be parsed, a `GedcomParseException` is thrown with a message describing the cause of the exception. However, the command line output might give additional information about the parsing process. The method `showParsingOutput()` can be used to show/hide output about all the parsed lines.

When parsing is done, all the structures are accessible through the `GedcomStore`. The first step is to get a `GedcomTree` of a certain structure from the store. The `GedcomTree` is the head node of the structure and contains one or more `GedcomNode`s.


The following code line gets an INDIVIDUAL_RECORD and creates recursively all the mandatory lines for that structure.

```java
GedcomTree tree = store.getGedcomTree("INDIVIDUAL_RECORD");
tree.addMandatoryChildLines(true);
```




Now, starting at the head of the tree, you can navigate throught the structure in two ways. Either step by step by using the `addChildLine`, `getChildLine`, `getParentLine` methods etc., or simply by using one of the the methods `followPath`, `followPathCreate`, `createPath`, `createPathEnd` (some of them also take care of any missing lines or create new paths).
The following lines show the usage with three examples.

```java
GedcomNode node1 = tree.getChildLine("INDI").addChildLine("PERSONAL_NAME_STRUCTURE").addChildLine("NAME");
GedcomNode node2 = tree.followPath("INDI", "PERSONAL_NAME_STRUCTURE", "NAME");
GedcomNode node3 = tree.followPathCreate("INDI", "INDIVIDUAL_ATTRIBUTE_STRUCTURE;RESI", "RESI", "INDIVIDUAL_EVENT_DETAIL", "EVENT_DETAIL", "ADDRESS_STRUCTURE", "ADDR"));

```

The first line shows how to follow the structure step by step (and adding the part PERSONAL_NAME_STRUCTURE and NAME). 
The second line shows how to use the more convenient way with `followPath`, which only works if the path already exists.
The third line shows a longer path which also includes a path step with multiple parts. Since the `INDIVIDUAL_ATTRIBUTE_STRUCTURE` has multiple variations, the tag given after the structure name specifies the required variation. Another detail about the third line is that the method `followPathCreate` is used. This method creates a new path if the given path does not exist.
The second and third ways are easier to use, but also slower (it always has to follow the whole path). However, a combination of the first and the second way is possible too of course.

*Hint: When accessing/creating lines, it is sometimes useful to print a node or the structure to see the whole structure and the building progress.*




For each `GedcomNode`, values can be set and retreived (as long as that line actually has a value or xref field). As defined in the lineage-linked grammar, a line can (but must not) have a value and/or a xref field. The value can be set with `setTagLineValue`, the xref with `setTagLineXRef` (the getter methods are `getTagLineXRef` and `getTagLineValue`).

```java
node.setTagLineValue("some value");
```


Now that we have created a little structure with some lines, it is time to print the structure. Since a `GedcomNode` is extended from `TreeNode` (from my [Util](http://github.com/thnaeff/Util) library), any of the `TreePrinter`s can be used to print the gedcom tree. However, `GedcomStore` comes with a few printers which are prepared for the use with the gedcom structures: `GedcomStructureTreePrinter`, `GedcomStructureTextPrinter` and `GedcomStructureHTMLPrinter`. See the "Key Features" section in this readme to get a short description of each printer.


```java
GedcomStructureTextPrinter textPrinter = new GedcomStructureTextPrinter(false);
System.out.println(textPrinter.print(tree));

GedcomStructureTreePrinter structureTreePrinter = new GedcomStructureTreePrinter();
System.out.println(structureTreePrinter.print(tree));
```


To print the `GedcomStore` and the lineage-linked grammar definitions stored in it, the `GedcomStorePrinter` class has to be used:

```java
System.out.println(GedcomStorePrinter.preparePrint(store, 2, false));
```

The data generated by the `GedcomStructureTextPrinter` printer can also be written to a file and imported into any software which supports the GEDCOM standard.

*Hint: A GEDCOM data file usually needs a HEADER, a SUBMITTER_RECORD, INDIVIDUAL_RECORDs and FAM_RECORDs and needs to end with the END_OF_FILE line `0 TRLR`*


********************************************************************************************************


## Examples
More examples can be found in the [test directory](src/ch/thn/gedcom/test/)


********************************************************************************************************


# Links to topics about GEDCOM
* [GEDCOM Documentation (auch in deutsch)](http://www.daubnet.com/de/gedcom)
* [The GEDCOM Standard Release 5.5](http://homepages.rootsweb.ancestry.com/~pmcbride/gedcom/55gctoc.htm)
* [GEnealogical Data COMmunication on Wikipedia](http://en.wikipedia.org/wiki/GEDCOM)
* [GEDCOM standard 5.5.1](http://www.phpgedview.net/ged551-5.pdf)
* [GEDCOM 5.5 Sample Page](http://heiner-eichmann.de/gedcom/gedcom.htm)
* [GEDCOM 5.5 Torture Test Files](http://www.geditcom.com/gedcom.html)
* [Genealogy infos, news and software](http://www.tamurajones.net/genealogy.xhtml)


********************************************************************************************************


# Not yet implemented
* Value validation -> It is possible to write your own value/xref validator by extending the `GedcomDataValidator` class and adding it to the `GedcomStore` with setValidator

********************************************************************************************************


# Dependencies
* [Joda-Time](http://http://www.joda.org)
* My own utility library: [Util](http://github.com/thnaeff/Util)


