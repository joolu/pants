# Copyright 2014 Pants project contributors (see CONTRIBUTORS.md).
# Licensed under the Apache License, Version 2.0 (see LICENSE).

python_library(
  name = 'task_registrar',
  sources = ['task_registrar.py'],
  dependencies = [
    '3rdparty/python:future',
    ':goal',
  ],
)

python_library(
  name = 'aggregated_timings',
  sources = ['aggregated_timings.py'],
  dependencies = [
    '3rdparty/python:future',
    'src/python/pants/util:dirutil',
  ]
)

# this is in goal because of run_tracker
python_library(
  name = 'artifact_cache_stats',
  sources = ['artifact_cache_stats.py'],
  dependencies = [
    '3rdparty/python:future',
    'src/python/pants/cache',
    'src/python/pants/util:dirutil',
  ]
)

python_library(
  name = 'context',
  sources = ['context.py'],
  dependencies = [
    '3rdparty/python:future',
    ':products',
    ':workspace',
    'src/python/pants/base:build_environment',
    'src/python/pants/build_graph', # XXX(fixme)
    'src/python/pants/base:deprecated',
    'src/python/pants/base:worker_pool',
    'src/python/pants/base:workunit',
    'src/python/pants/java/distribution:distribution',
    'src/python/pants/process',
    'src/python/pants/reporting:report',
    'src/python/pants/source',
  ],
)

python_library(
  name = 'error',
  sources = ['error.py'],
)

# TODO(benjy): As a result of a renaming, we ended up with this target owning only one source
# file, whereas its name might indicate to the casual reader that it represents the entire package.
# However the targets in this BUILD file (and elsewhere) are probably too fine-grained anyway, so
# we may just solve this possible confusion by refactoring out some stuff and unifying the rest.
python_library(
  sources = ['goal.py'],
  dependencies = [
    '3rdparty/python:future',
    ':error',
    'src/python/pants/option',
    'src/python/pants/util:memo',
  ],
)

python_library(
  name = 'pantsd_stats',
  sources = ['pantsd_stats.py'],
  dependencies = [
    '3rdparty/python:future',
  ],
)

python_library(
  name = 'products',
  sources = ['products.py'],
  dependencies = [
    '3rdparty/python:future',
    '3rdparty/python/twitter/commons:twitter.common.collections',
    'src/python/pants/util:collections',
    'src/python/pants/util:dirutil',
  ],
)

python_library(
  name = 'run_tracker',
  sources = ['run_tracker.py'],
  dependencies = [
    '3rdparty/python:future',
    ':aggregated_timings',
    ':artifact_cache_stats',
    ':pantsd_stats',
    '3rdparty/python:requests',
    '3rdparty/python:pyopenssl',
    'src/python/pants/auth',
    'src/python/pants/base:build_environment',
    'src/python/pants/base:run_info',
    'src/python/pants/base:worker_pool',
    'src/python/pants/base:workunit',
    'src/python/pants/option',
    'src/python/pants/reporting', # XXX(fixme)
    'src/python/pants/subsystem',
    'src/python/pants/util:dirutil',
    'src/python/pants/util:memo',
  ],
)

python_library(
  name = 'workspace',
  sources = ['workspace.py'],
  dependencies = [
    'src/python/pants/base:build_environment',
    'src/python/pants/scm',
    'src/python/pants/util:meta',
  ],
)
