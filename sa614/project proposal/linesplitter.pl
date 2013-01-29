#!/usr/bin/perl

# COPYRIGHT 29th JANUARY 2013 - M. T. IRELAND
# PLEASE DO NOT MODIFY, COPY OR REDISTRIBUTE WITHOUT THE EXPLICIT, WRITTEN
# PERMISSION OF THE AUTHOR
# THIS FILE COMES WITH NO GUARANTEE

# USEAGE: perl linesplitter.pl <input file> <output file>

open INPUT, "<", $ARGV[0] or die "Could not open input file for reading!";
open OUTPUT, ">", $ARGV[1] or die "Could not open output file for writing!";

while (<INPUT>) {
    s/\.\s/.\n/g;
    print OUTPUT $_;
}

close INPUT;
close OUTPUT;
