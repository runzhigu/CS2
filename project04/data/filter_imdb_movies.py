# To update data each year:
# Download title.basics.tsv and title.ratings.tsv from https://datasets.imdbws.com/
# Run title.basics.tsv through this script to filter it to movies only.
# This is important - it's a _much_ smaller set!
# Replace the old movies.basics.tsv and title.ratings.tsv with these new files.
# Make sure to not put title.basics.tsv in the repo, it's large.

# You'll also want to update the "test_..." files appropriately.
# I did this by hand by dropping the output of the reference into the test files.

import csv


movies = []

with open("title.basics.tsv") as infile:
    reader = csv.DictReader(infile, delimiter="\t")
    fieldnames = reader.fieldnames
    for row in reader:
        if row["titleType"] == "movie":
            movies.append(row)

with open("movies.basics.tsv", "w") as outfile:
    writer = csv.DictWriter(outfile, fieldnames=fieldnames, delimiter="\t")
    for row in movies:
        writer.writerow(row)