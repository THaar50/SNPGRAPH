# snpgraph
Tool for visualizing SNP distributions across transcription factor binding sites in the human genome developed during my bachelor's thesis in 2018.

# Usage
The base call to snpgraph looks like this on Linux:
```
java -jar snpgraph-0.1.0.jar data/filename
```

For Windows, the supplied [Dockerfile](Dockerfile) can be used to build and run a container with the following command:
```
docker build -t snpgraph .
docker run --mount src="$(pwd)"/graph,target=/snpgraph/build/libs/graph,type=bind --mount src="$(pwd)"/data,target=/snpgraph/data,type=bind -it snpgraph /snpgraph/data/HeLa-S3_NFYA_narrowPeak.bed
```

# Visualization
A visualization of SNP distributions for the provided ChipSeq data file [HeLa-S3_NFYA_narrowPeak.bed](data/HeLa-S3_NFYA_narrowPeak.bed) looks like this:
![](graph/HeLa-S3_NFYA_narrowPeak.png)