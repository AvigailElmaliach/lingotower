INSERT INTO category (name)
SELECT 'אוכל'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'קטגוריה 1');

INSERT INTO category (name)
SELECT 'מילים בסיסיות'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'קטגוריה 15');

INSERT INTO category (name)
SELECT 'טיולים וטיסות'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'קטגוריה 2');
