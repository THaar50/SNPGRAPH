# snpgraph
Tool for visualizing SNP distributions across transcription factor binding sites in the human genome developed during my bachelor's thesis in 2018.

# Usage
The base call to snpgraph looks like this on Linux:
```
java -jar snpgraph-0.1.0.jar
```

# Pre-requisites
1. Create folder snpgraph
```
mkdir snpgraph
```
2. Clone the repository:
```
git clone https://github.com/THaar50/snpgraph
```
3. Run snpgraph:

- On Windows: 
The supplied [Dockerfile](Dockerfile) can be used to build and run a container with the following command:
```
docker build -t snpgraph .
docker run --mount src="$(pwd)"/graph,target=/snpgraph/build/libs/graph,type=bind --mount src="$(pwd)"/data,target=/snpgraph/data,type=bind -it snpgraph
```
- On Linux:
Build the project with gradle and then run the application from the build/libs folder.
```
./gradlew build
cd ./build/libs/
java -jar snpgraph-0.1.0.jar
```

# Visualization
A visualization of SNP distributions for the provided ChipSeq data file [HeLa-S3_NFYA_narrowPeak.bed](data/chipseq/HeLa-S3_NFYA_narrowPeak.bed) looks like this:
![](graph/HeLa-S3_NFYA_narrowPeak.png)