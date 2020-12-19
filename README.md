GrabCraft is a site that has tons of MineCraft builds to build yourself and use in your own worlds. However, its schema display is quite small, and only shows colors for different block types (so you need to mouse hover every single one to see what it is), making it extremely hard to build something from their specifications.

This mod helps modernizing their build plans by converting them into .litematic files, which can be read and uses directly by Litematica.

Navigate the GrabCraft site to the build you're interested in, copy the URL, paste it into this mod, and you'll get a nice Litematica file you can open directly.

Here's a video to show how it works:

https://www.youtube.com/watch?v=b7BkZH3HATg

To open the download GUI, you'll need to press the Y key - can be configured in Options/Controls.

----------------------------------------------------------------------------------------

**Rotation of stairs, rails, ladders, ...., incorrect?**

Unfortunately, GrabCraft uses its own description list which needs manual mapping to MineCraft internals. My mapping file contains over 1000 rows of "which GrabCraft description corresponds to which Minecraft description", but some details are missing. Even worse, GrabCraft isn't consistent - a stair that's "low" on the south side and "high" on the north side has a "north stair" description in some GrabCraft models, and a "south stair" description in others. 

I will, eventually, add a "swap north/south and east/west" checkbox to the mod so you can import "the other way round" if the default doesn't work.

Also, if you're technically inclined, you're welcome to send improvements to my mapping file on GitHub - see there: https://github.com/gbl/GrabcraftLitematic/blob/master/blockmap.csv

---------------------------------------------------------------------------------------

**Getting an error: "sun.security.validator.ValidatorException: PKIX path building"?**

This happens with the version of Java that comes with Minecraft, which is quite old - too old to understand the security certificate that's used by GrabCraft.

The version 0.3 of this mod fixes that problem by adding in a newer certificate. Even when that version says "1.16.4" because it was compiled against 1.16.4 Minecraft, it works from 1.16.2 on!

So, use the 1.16.4....0.3 version of the mod, it will work from MC 1.16.2 on and fix the ValidatorException problem.

----------------------------------------------------------------------------------------
