INSERT INTO models (id, name) VALUES (1, 'model_id');

INSERT INTO depots (id, name, latitude, longitude, address, model_id) VALUES (0, 'depot', 37.9873, 23.7581, 'Λεωφόρος Αλεξάνδρας 203', 1);

INSERT INTO vehicles (id, name, capacity, model_id) VALUES (1, 'v1', 20, 1);
INSERT INTO vehicles (id, name, capacity, model_id) VALUES (2, 'v2', 40, 1);
INSERT INTO vehicles (id, name, capacity, model_id) VALUES (3, 'v77', 10, 1);

INSERT INTO customers (id, name, demand, latitude, longitude, address, model_id) VALUES (1, 'c1', 14, 37.9905, 23.7612, 'Αργολίδος 42', 1);
INSERT INTO customers (id, name, demand, latitude, longitude, address, model_id) VALUES(2, 'c2', 12, 37.9903, 23.7572, 'Αλφειού 7', 1);
INSERT INTO customers (id, name, demand, latitude, longitude, address, model_id) VALUES(3, 'c3', 8, 37.9924, 23.7545, 'Πριήνης 18', 1);
