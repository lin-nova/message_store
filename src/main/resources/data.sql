INSERT INTO clients(uuid, username) VALUES ('4d0bc6b2-3252-43e2-ba14-fd1e334018b1', 'client1');
INSERT INTO clients(uuid, username) VALUES ('a69ddb59-f58a-4bad-8122-db0934d2b2fa', 'client2');

INSERT INTO messages(uuid, client_id, content) VALUES ('7aa44b54-c479-438b-8bfb-7d205f5357fc', '4d0bc6b2-3252-43e2-ba14-fd1e334018b1', 'Predefined Message #1 for client1');
INSERT INTO messages(uuid, client_id, content) VALUES ('e26de555-311f-4602-9518-ab9ebd9c2c93', '4d0bc6b2-3252-43e2-ba14-fd1e334018b1', 'Predefined Message #2 for client1');
INSERT INTO messages(uuid, client_id, content) VALUES ('57b34ecd-0095-462a-a22e-5983d1413dad', 'a69ddb59-f58a-4bad-8122-db0934d2b2fa', 'Predefined Message #1 for client2');