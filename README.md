PathwayMatrix
=============
PathwayMatrix is a visualization tool that presents the binary relations between proteins in the pathway via the use of an interactive adjacency matrix. The glyph inside each circular cell indicates concurrent relations between a pair of proteins. PathwayMatrix imports pathway data in BioPAX format.

PathwayMatrix supports interactive capabilities to help users interested in exploring very dense pathway networks. The ability to order by protein arranges similar proteins so that they appear together in the visualization. Smooth lensing allows the viewer to focus on a particular protein or a group of proteins that is of interest. Finally, grouping similar proteins provide a more compressed view of the entire network.

The following figure shows two different views of the HIV life cycle (contains 76 proteins and 11,337 binary relations) in PathwayMatrix. In the first panel, proteins have been ordered by similarity. Two proteins are considered to be similar if they perform the same functionalities, and therefore have same sets of interactions to other proteins in the pathway. Ordering enables the identification of modules or sub-networks within large pathways. In the second panel, similar proteins are grouped together to provide a compressed view of the network. Backgrounds of rows and columns are colored by group sizes.

![ScreenShot](http://www.cs.uic.edu/~tdang/PathwayMatrix/TearserImage.png)

Here are more examples:

The data is the RAF Cascade pathway. A Venn diagram is supplied on  the right to provide an overview of the types of interactions within the pathway. The size of circles in this diagram represents the frequency of different relations in the pathway, and overlapping areas provide an overview of how often relations co-exist in the pathway. We use the same color encoding for the Venn diagram and the matrix. For example, red represents "controls-state-change-of" relationships and blue represents "neighbor of" relationships.
![ScreenShot](http://www.cs.uic.edu/~tdang/PathwayMatrix/Image1-RAF%20Cascade.png)

In the next figure, the data is the Influenza Infection pathway.
![ScreenShot](http://www.cs.uic.edu/~tdang/PathwayMatrix/Image2-Influenza%20Infection.png)

In the next figure, the data is the ERBB2 pathway.  Lensing is applied on a middle section of the matrix.
![ScreenShot](http://www.cs.uic.edu/~tdang/PathwayMatrix/Image3-ERBB2.png)

In the next figure, the data is the Replication of DNA pathway. The protein ordering algorithm enables similar proteins to locate next to each other to reveal cluster structures.

![ScreenShot](http://www.cs.uic.edu/~tdang/PathwayMatrix/Image4-Replication%20of%20DNA.png)

In the next figure, the data is the Signaling to TGF pathway.
![ScreenShot](http://www.cs.uic.edu/~tdang/PathwayMatrix/Image5-Signalling%20to%20TGF.png)


Please click to watch the overview video.

[![ScreenShot](http://www.cs.uic.edu/~tdang/PathwayMatrix/TeaserVideo.png)](http://www2.cs.uic.edu/~tdang/PathwayMatrix/PathwayMatrixBioVis960x540.mp4)

The application (PathwayMatrix_1_1.jar) is available in application.Cross-Platform folder.

This work was funded by the DARPA Big Mechanism Program under ARO contract WF911NF-14-1-0395.










