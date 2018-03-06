#!/bin/bash
# This file performs a few tests for program in general

out_dir=out/artifacts/pack_rle_jar
texts_dir=example

# Delete files from example
if [ "$1" == "clear" ]
then
    rm ${texts_dir}/packed_file ${texts_dir}/text2.txt.rle ${texts_dir}/unpacked_file ${texts_dir}/text2.txt
    exit
fi

# Try to get the same data after pack-unpack via the pipe
some_data="xgfjf00000000000mhfvt8htn hjgfhgefbjksrhxoy8l nis h.d,cvjlk hnkgngdjkdgfjjjjjjjjjjjjjjjjjjjjjfxedtgtggg"
some_new_data=$(echo ${some_data} | java -jar ${out_dir}/pack-rle.jar -zio | java -jar ${out_dir}/pack-rle.jar -uio)
if [ "$some_new_data" == "$some_data" ]
then
    echo "OK"
else
    echo 'Expected:'
    echo "    \"$some_data\""
    echo ""
    echo 'Actually:'
    echo "    \"$some_new_data\""
    echo ""
fi

# Try all possible arguments
java -jar ${out_dir}/pack-rle.jar -z ${texts_dir}/text.txt
java -jar ${out_dir}/pack-rle.jar -z -out ${texts_dir}/packed_file ${texts_dir}/text.txt
java -jar ${out_dir}/pack-rle.jar -u -out ${texts_dir}/unpacked_file ${texts_dir}/packed_file
mv ${texts_dir}/text.txt.rle ${texts_dir}/text2.txt.rle
java -jar ${out_dir}/pack-rle.jar -u ${texts_dir}/text2.txt.rle

# No differences are expected
diff ${texts_dir}/unpacked_file ${texts_dir}/text.txt
diff ${texts_dir}/text2.txt ${texts_dir}/text.txt

# Write the sizes of all out files
ls -l ${texts_dir}
