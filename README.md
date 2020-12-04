# Fuzzy-C-Means-Clustering

This is a tiny module of a project that I, along with a teammate, were assigned during our three-month rigorous winter internship at one of the Indian Space Research Organization's research institute- IIRS Dehradun.

Various LEO & MEO satellites at ISRO have been acquiring data and sending it down, but none of that matters unless we use it to identify and solve hard-to-think problems while staying at the ground.

The Maps team has already tackled one such problem at Google via their tremendous efforts over these years- to effectively differentiate between various 'land cover types' like roads, vegetation, water, buildings, etc. and "cluster" them all in a way that's soothing to the eye, and operational for their product.

However, there is so much more to this 'classification problem' than just building a lovely Maps product.

In theory, Remote Sensing tells us that different types of vegetation covers exhibit different 'levels' of spectral reflectance values when illuminated via EM energies. Thus, a football field grass would have a darker (dimmer) greyscale value than natural vegetation (brighter) under a particular EM spectrum wavelength. 

Does this mean that we can identify malnutrition soil against good soil, similar to what we did above? 
Moreover, can we differentiate between healthy and unhealthy crops? 
All of it *without* physically going to a place, taking samples, and sending them to a lab? Saving so much time?!
# Absolutely YES!

And that is just one of the numerous applications of the end result of this project!

All of the above is possible only once we could effectively create clusters while taking only minimal input from the human operator (algorithm input parameters). We implement the False Color Composite technique to resolve and understand the image-data, and finally, the Fuzzy C Means algorithm to 'cluster' an n-band satellite image into constituent clusters, all in BIL raster format.

I have open-sourced the module for you to view, run and analyze, while I appreciate all kinds of comments and improvements!
