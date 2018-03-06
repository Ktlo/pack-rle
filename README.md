# pack-rle
The RLE algorithm based compressor for UTF-8 text files.

### Usage
```
java -jar pack-rle.jar -[z|u] [-out output_filename] input_filename
```
#### -z:
Compress file using RLE.
#### -u:
Decompress file.

#### -out output_filename:
Changes the output file to the specified one. The default output filename is ```<input_filename>.rle``` when compressing
and input filename without ```.RLE``` extension when decompressing.

#### input_filename:
File to compress or decompress.

### Usage with pipe
```
java -jar pack_rle.jar -[z|u]i [-out output_filename]
java -jar pack_rle.jar -[z|u]o input_filename
java -jar pack_rle.jar -[z|u]io
```
You can also redirect the input to the standart stream with ```-i``` parameter and output with ```-o``` parameter.

### File format

Program expects the UTF-8 text that has't '\0' character.
Each substring of the same characters which length is >= 4 swaps to three UTF-8 characters: '\0', character code that equal to the length
of the string and the repeated character.
For example "pogw777777lk" turns to "pogw\000\0067lk".
