# GedcomStore
**A library to parse a [lineage-linked grammar file](http://homepages.rootsweb.ancestry.com/~pmcbride/gedcom/55gcch2.htm) and build a valid [GEDCOM](http://en.wikipedia.org/wiki/GEDCOM)-structure according to the parsed definitions**

GedcomStore is a library written in Java to parse lineage-linked grammar from a text file. It then gives access to the parsed structures in order to create a valid GEDCOM ouput.
Since the structures are created according to the grammar file, the output does not have to be validated against the GEDCOM structure - only valid ouput which matches the given input file can be created.


## Key features
* Create your own gedcom definition file using lineage-linked grammar (preferably, take one of the "standards" and modify them to your needs if necessary - see the next point)
* Comes with the gedcom definition files for version 5.5 (gedcomobjects_5.5.gedg) and 5.5.1 (gedcomobjects_5.5.1.gedg), plus a slightly modified version for the [GRAMPS](http://gramps-project.org) software (gedcomobjects_5.5.1_gramps.gedg)
* Supports ALL the fields in the GEDCOM lineage-linked grammar (See "Not yet implemented" for details)
* Choose if you want structures to be automatically populated with all mandatory lines, with all available lines or no lines
* If structures are not populated with all lines, they are only created when needed
* Easy creation and access to the structures, lines and values
	* Just define the "path" to the needed line by using an array (or variable argument string), like: `"INDI", "REFN", "TYPE", ...`
	* It automatically follows the given path and creates any missing structures or lines if needed or requested
	* If a path is invalid and can not be followed, it tries to give hints on what the next path item could possibly be
	* For speed optimization, "paths" can be followed step by step
* Simple output generation with the `GedcomToString` class
	* Choose if empty lines (with an empty value set) should be printed or not
	* Choose if empty lines (with no value set at all) should be printed or not
	* Pick any block/line to start printing from
	* Choose the maximum depth level of the output
	* Choose if linked structures should be included in the ouput or not
	* Print all or parts of the parsed lineage-linked grammar file


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

Such a block, written in a *.gedg file, can be parsed with `GedcomStore` and instances of it can be retrieved to add lines and set values. The retrieved instances can then be converted to a string with `GedcomToString`.
Such an output with values can look like the following (only lines with values are printed):
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

When parsing is done, all the structures are accessible through the `GedcomStore`. The first step is to get a starting block of a certain structure from the store. A block contains one or more lines, and each line has it's own block (if there are any child-lines available).


The following code line gets an INDIVIDUAL_RECORD. There are three modes available: `ADD_MANDATORY`, `ADD_ALL` and `ADD_NONE`. Each of them defines how the structure is initially created. `ADD_MANDATORY` for example creates a structure with all the mandatory lines already added.

```java
GedcomBlock block = store.getGedcomBlock("INDIVIDUAL_RECORD", GedcomBlock.ADD_MANDATORY);
```




Now, with the structure block, you can navigate throught the structure in two ways. Either step by step by using `getChildLine()`, `getParentLine()`, `getParentBlock()`, `addStructure()`, `addTagLine()` etc. or simply by using the method `followPath()` which also takes care of any missing lines.
The following lines show the usage of those two ways.

```java
GedcomLine line = block.getChildLine("INDI").getChildBlock().addStructureLine("PERSONAL_NAME_STRUCTURE").getChildLine("NAME");
GedcomObject object = block.followPath("INDI", "PERSONAL_NAME_STRUCTURE", "NAME");
GedcomObject object2 = block.followPath(true, "INDI", "INDIVIDUAL_ATTRIBUTE_STRUCTURE;RESI", "RESI", "INDIVIDUAL_EVENT_DETAIL", "EVENT_DETAIL", "ADDRESS_STRUCTURE", "ADDR"));

```

The first line shows how to follow the structure step by step (and adding the missing part PERSONAL_NAME_STRUCTURE to the block of a line). 
The second line shows how to use the more convenient way with `followPath()`, where missing parts are created automatically. If the path already exists, the existing gedcom object is returned.
The third line shows a longer path which also includes a path step with multiple parts. Since the `INDIVIDUAL_ATTRIBUTE_STRUCTURE` has multiple variations, the tag given after the structure name specifies the required variation. Another detail about the third line is the `true` flag as first argument of the method. With this flag set to `true`, a new path will be created and the returned gedcom block will be a new one.
The second and third ways are easier to use, but also slower (it always has to follow the whole path). However, a combination of the first and the second way is possible too of course, since each `GedcomLine` or `GedcomBlock` is a subclass of `GedcomObject`, which implements the followPath method.

*Hint: When accessing/creating lines, supporting output is given on the command line showing the accessed and added lines. It can be turned off with `store.showAccessOutput(false)`*




If a `GedcomLine` (or `GedcomTagLine`) is returned (which is a subclass of `GedcomObject`), values can be set and retreived. As defined in the lineage-linked grammar, a line can (but must not) have a value and/or a xref. The value can be set with `setValue`, the xref with `setXRef` (the getter methods are `getValue` and `getXRef`).

```java
line.setValue("some value");
```

If a `GedcomObject` is returned, it could be cast to a `GedcomTagLine` (if it is an instance of course), or the more convenient way below can be used which returns the right class (or throws an exception if the object is not a tag line).

```java
object.getAsTagLine().setValue("some other value");
```

There are also such methos for lines, structure lines and blocks: `getAsLine()`, `getAsStructureLine()`, `getAsBlock()`.

Now that we have created a little structure with some lines, it is time to print it. The class `GedcomToString` helps with that.
Here are two possibilities to print a line itself and a line with it's child-lines. Using the examples does not produce a very exciting output since the returned line does not have any child lines.

```java
System.out.println(GedcomToString.preparePrint(line));
System.out.println(GedcomToString.preparePrint(line, 2, false, true));
```

or a whole block, including all empty lines and lines for which no value/xref has been set...

```java
System.out.println(GedcomToString.preparePrint(object.getParentBlock(), 0, true, true, true));
```

or how about the whole store, limited to level 2...

```java
System.out.println(GedcomToString.preparePrint(store, 2, false));
```

The returned date generated with `GedcomToString` can then be written to a file and imported into any software which supports the GEDCOM standard.

*Hint: A GEDCOM data file usually needs a HEADER, a SUBMITTER_RECORD, INDIVIDUAL_RECORDs and FAM_RECORDs and needs to end with the END_OF_FILE line `0 TRLR`*


********************************************************************************************************


## Examples
More examples can be found in src/ch/thn/gedcom/test/


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
* Value validation (Some values only allow certain formats/lengths/...)

********************************************************************************************************


# Dependencies
* [Joda-Time](http://http://www.joda.org)
* My own utility library: [Util](http://github.com/thnaeff/Util)


