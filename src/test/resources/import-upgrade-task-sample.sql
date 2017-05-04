INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (1,
  '5303f31d-1c55-4395-8873-0b6c06c16ec3',
  'UPGRADE_2_0_DOMAIN_UUID',
  'UPGRADE_2_0',
  null,
  null,
  1,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);


INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (2,
  '838f6b35-df62-4a5d-aafa-749581a2ee33',
  'UPGRADE_2_0_DOMAIN_QUOTA',
  'UPGRADE_2_0',
  null,
  null,
  2,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (3,
  '8705ccae-84ea-493b-8c1d-ee45b49a3eca',
  'UPGRADE_2_0_ACCOUNT_QUOTA',
  'UPGRADE_2_0',
  '838f6b35-df62-4a5d-aafa-749581a2ee33',
  'UPGRADE_2_0_DOMAIN_QUOTA',
  3,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (4,
  'aaba2767-bc5a-4244-9d42-d0f14ff79c98',
  'UPGRADE_2_0_RESTRICTED_CONTACT',
  'UPGRADE_2_0',
  null,
  null,
  4,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (5,
  'a006d5be-4605-4a14-b292-7f34fa12c690',
  'UPGRADE_2_0_STORAGE',
  'UPGRADE_2_0',
  null,
  null,
  5,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);

