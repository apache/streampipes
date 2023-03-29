#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

import io
import os

import setuptools

# Package meta-data.
NAME = "streampipes"
DESCRIPTION = "Python library for Apache StreamPipes"
LONG_DESCRIPTION_CONTENT_TYPE = "text/markdown"
URL = "https://streampipes.apache.org/docs/docs/python/latest/"
EMAIL = "dev@streampipes.apache.org"
AUTHOR = "Apache Software Foundation"
REQUIRES_PYTHON = ">=3.8.0"

PROJECT_URLS = {
    "Documentation": "https://streampipes.apache.org/docs/docs/python/latest/",
    "Bug Tracker": "https://github.com/apache/streampipes/issues",
    "Source Code": "https://github.com/apache/streampipes",
}

# Package requirements.
base_packages = [
    "pandas>=1.5.1",
    "pydantic>=1.10.2",
    "requests>=2.28.1",
    "nats-py>=2.2.0",
    "confluent-kafka>=2.0.2"
]

dev_packages = base_packages + [
    "autoflake==2.0.0",
    "black==23.3.0",
    "blacken-docs==1.13.0",
    "flake8==6.0.0",
    "interrogate==1.5.0",
    "isort==5.12.0",
    "mypy==1.1.1",
    "pandas-stubs==1.5.2.230105",
    "pre-commit==3.2.0",
    "pytest==7.2.1",
    "pytest-cov==4.0.0",
    "pyupgrade==3.3.1",
    "types-Jinja2==2.11.9",
    "types-requests==2.28.11.7",
]

docs_packages = [
    "mkdocs==1.4.2",
    "mkdocs-awesome-pages-plugin==2.8.0",
    "mkdocs-material==9.1.3",
    "mkdocstrings[python]==0.20.0",
    "pytkdocs[numpy-style]>=0.16.1",
    "mkdocs-gen-files==0.4.0",
    "mkdocs-literate-nav==0.6.0",
    "numpydoc==1.5.0",
    "mkdocs-jupyter==0.24.0",
    "mike @ git+https://github.com/jimporter/mike.git@872f72def32f588908f8251fe512189e0c41f4e2"
]

here = os.path.abspath(os.path.dirname(__file__))

# Import the README and use it as the long-description.
with io.open(os.path.join(here, "README.md"), encoding="utf-8") as f:
    long_description = "\n" + f.read()

# Load the package's __version__.py module as a dictionary.
about = {}
with open(os.path.join(here, "streampipes", "__version__.py")) as f:
    exec(f.read(), about)

# Where the magic happens:
setuptools.setup(
    name=NAME,
    version=about["__version__"],
    description=DESCRIPTION,
    long_description=long_description,
    long_description_content_type=LONG_DESCRIPTION_CONTENT_TYPE,
    author=AUTHOR,
    author_email=EMAIL,
    python_requires=REQUIRES_PYTHON,
    url=URL,
    project_urls=PROJECT_URLS,
    packages=setuptools.find_packages(exclude=("tests",)),
    install_requires=base_packages,
    extras_require={
        "dev": dev_packages,
        "test": dev_packages,
        "docs": docs_packages,
        "all": dev_packages + docs_packages,
    },
    include_package_data=True,
    license="Apache License 2.0",
    classifiers=[
        # Trove classifiers
        # Full list: https://pypi.python.org/pypi?%3Aaction=list_classifiers
        "Development Status :: 4 - Beta",
        "Environment :: Console",
        "Intended Audience :: Developers",
        "Intended Audience :: Information Technology",
        "Intended Audience :: Manufacturing",
        "License :: OSI Approved :: Apache Software License",
        "Operating System :: OS Independent",
        "Programming Language :: Python",
        "Programming Language :: Python :: 3",
        "Programming Language :: Python :: 3.8",
        "Programming Language :: Python :: 3.9",
        "Programming Language :: Python :: 3.10",
        "Topic :: Scientific/Engineering"
    ],
    ext_modules=[],
    keywords='streampipes, iot, iiot, analytics, stream-processing, apache',
)
