# Changelog

2.0.0
---

**Added**
- Added Curios support for the Armor Banner

**Changed**
- Betsy Ross now depends on Immersive Paintings
  - This will break old paintings.
- The Flag block item no longer holds images and must be set by (shift-)right-clicking on the in-world block
- Crafting the actual flag items is no longer done in the Embroidery Table.
  - The items are now crafted in the Crafting Table.
  - Flag item crafting is no longer configured through the server
  - All crafting is now done through regular crafting recipes
- The Embroidery Table is now only limited to setting images for item flags.
  - It is now accessed by right-clicking with the Flag Standard, Banner Standard, or Armor Banner
- Flag items no longer display their texture in the inventory, in item frames, or on the ground
- Flag dimension limits now apply when creating flags in the Flag block and Embroidery Table screens
  - Config for each of the four item types remains.

**Fixed**
- Fixed flag block drops
- Fixed flag blocks occluding skylight
