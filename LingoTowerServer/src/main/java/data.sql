INSERT INTO category (name) VALUES ('קטגוריה 1');
INSERT INTO category (name) VALUES ('קטגוריה 15');
INSERT INTO category (name) VALUES ('קטגוריה 2');




/*INSERT INTO user (username, password, language) VALUES ('user1', 'password1', 'עברית');
INSERT INTO word (word, translation, category_id) 
VALUES ('מילה', 'תרגום', (SELECT id FROM category WHERE name = 'קטגוריה 1' LIMIT 1));*/
INSERT INTO user (username, password, language) 
VALUES ('user1', 'password1', 'עברית');
INSERT INTO user (username, password, language) 
VALUES ('user2', 'password2', 'אנגלית');

