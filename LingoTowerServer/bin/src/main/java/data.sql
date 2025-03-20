INSERT INTO category (name)
SELECT 'אוכל'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'אוכל');

INSERT INTO category (name)
SELECT 'מילים בסיסיות'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'מילים בסיסיות');

INSERT INTO category (name)
SELECT 'טיולים וטיסות'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'טיולים וטיסות');
 

