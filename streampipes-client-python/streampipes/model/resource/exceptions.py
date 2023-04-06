from __future__ import annotations

from typing import Optional


class StreamPipesUnsupportedDataLakeSeries(Exception):
    """Exception to be raised when the returned data lake series
    cannot be parsed with the current implementation of the resource.
    """

    def __init__(self, reason: Optional[str] = None):
        super().__init__(
            "The Data Lake series returned by the API appears "
            "to have a structure that is not currently supported by the Python client."
            f"Reason: {reason}"
        )
