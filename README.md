# Fuzzy-C-Means-Clustering

This is a tiny module of a project that I along with a team mate, were assigned during our 2 month rigorous winter internship at one of Indian Space Research Organization's research institute- IIRS Dehradun.

Various LEO & MEO satellites at ISRO have been acquiring a lot of data and sending it down, but none of that really matters unless we use it to identify and solve problems that are hard to think of while staying at ground.

One such problem that has already been tackled by the Maps team at Google via their tremendous efforts over these years- is to effectively differentiate between various 'land cover types' like roads, vegetation, water, buildings, etc. and "clusterize" them all in a way that's soothing to the eye, and operational for their product.

But there's so much more to this 'classification problem' than just building a lovely Maps product.

In theory, Remote Sensing tells us that different types of vegetation covers exhibit different 'levels' of spectral reflectance values when illuminated via EM energies. Thus, a football field grass would have a darker (dimmer) greyscale value than natural vegetation (brighter) under a particular wavelength of EM spectrum. 

Does this mean that we can identify malnutritioned soil against a good soil similar to what we did above? 
And can we differentiate between healthy and unhealthy crops? 
All of it *without* physically going to a place, taking samples and sending them to a lab? Saving so much time?!
# Absolutely YES!

And that's just one of the numerous applications of the end result of this project !

All of the above is possible only once we could effectively create clusters while taking only minimal input from the human operator. (algo input parameters.) We implemented the False Color Composite technique to resolve and understand the image-data, and finally the Fuzzy C Means algorithm to 'clusterize' a 4-band satellite image into constituent clusters, all in BIL raster format.

I've open-sourced the module for you to view, run and analyse, while I appreciate all kinds of comments and improvements!
