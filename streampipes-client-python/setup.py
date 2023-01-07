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
NAME = "streampipes_client"
DESCRIPTION = "Python client for Apache StreamPipes"
LONG_DESCRIPTION_CONTENT_TYPE = "text/markdown"
URL = "https://github.com/apache/streampipes/"
EMAIL = "dev@streampipes.apache.org"
AUTHOR = "Apache Software Foundation"
REQUIRES_PYTHON = ">=3.8.0"

# Package requirements.
base_packages = [
    "pandas>=1.5.1",
    "pydantic>=1.10.2",
    "requests>=2.28.1",
    "nats-py>=2.2.0",
]

dev_packages = base_packages + [
    "autoflake>=1.7.7",
    "black>=22.10.0",
    "flake8>=5.0.4",
    "interrogate>=1.5.0",
    "isort>=5.10.1",
    "mypy>=0.990",
    "pandas-stubs>=1.2.0.62",
    "pre-commit>=2.20.0",
    "pytest>=7.2.0",
    "pytest-cov>=4.0.0",
    "pyupgrade>=3.2.2",
    "types-requests>=2.28.11.4",
]

docs_packages = [
    "mkdocs>=1.2.3",
    "mkdocs-awesome-pages-plugin>=2.7.0",
    "mkdocs-material>=8.1.11",
    "mkdocstrings[python]>=0.19.0",
    "pytkdocs[numpy-style]>=0.5.0",
    "mkdocs-gen-files>=0.3.5",
    "mkdocs-literate-nav>=0.4.1",
    "numpydoc>=1.2",
    "mkdocs-jupyter>=0.22.0 "
]

here = os.path.abspath(os.path.dirname(__file__))

# Import the README and use it as the long-description.
with io.open(os.path.join(here, "README.md"), encoding="utf-8") as f:
    long_description = "\n" + f.read()

# Load the package's __version__.py module as a dictionary.
about = {}
with open(os.path.join(here, NAME, "__version__.py")) as f:
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
        "Development Status :: 3 - Alpha",
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
        "Programming Language :: Python :: 3.11",
        "Topic :: Scientific/Engineering"
    ],
    ext_modules=[],
    keywords='streampipes, iot, iiot, analytics, stream-processing, apache',
)
