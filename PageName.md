# Activity Flow #

## Introduction ##

This doc lists the activity flow of this application.


## Details ##
**MainActivity** is the main activity class which gets invoked when the application starts.
This activity then invokes the various other activities as per user selection.
Following are the list of Activity classes:
|Activity Name|**Purpose**|
|:------------|:----------|
|MainActivity |Main activity class which is invoked when application starts|
|BrowsePDFActivity|For browsing files|
|ConcatenatePDFActivity|For concatenating two PDFs|
|FileToPDFActivity|Convert TXT/HTML to PDF|
|ImageToPDFActivity|Coverting any image to PDF|
|OpenSNPDFFolderActivity|Opening prepared PDFs|
|PickedPDFActivity|Showing the chosen PDF from prepared PDFs list|
|PickNumberActivity|Number input - for pages|
|PickPasswordActivity|PDF password input|
|ProtectPDFActivity|For protecting a PDF|
|SNPDFActivity|Super class for all Activity classes|
|SplitActivity|For extracting pages from a PDF|
|About        |Showing About page|
|FAQActivity  |Showing FAQs page|

The project structure follows the standard Android structure.