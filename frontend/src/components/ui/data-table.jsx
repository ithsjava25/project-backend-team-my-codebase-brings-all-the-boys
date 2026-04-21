import {
    flexRender,
    getCoreRowModel,
    getPaginationRowModel,
    useReactTable,
} from "@tanstack/react-table";

import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";

import {Button} from "@/components/ui/button";

export function DataTable({
                              columns,
                              data,
                              page,
                              setPage,
                              pageSize,
                              setPageSize,
                              totalPages,
                          }) {
    const table = useReactTable({
        data,
        columns,
        getCoreRowModel: getCoreRowModel(),
        manualPagination: true,
        pageCount: totalPages,
        state: {
            pagination: {
                pageIndex: page,
                pageSize: pageSize,
            },
        },
    });

    const displayTotalPages = Math.max(totalPages, 1);
    const isEmpty = totalPages === 0;

    return (
        <div className="space-y-4">

            {/* TABLE */}
            <div className="rounded-md border">
                <Table>
                    <TableHeader>
                        {table.getHeaderGroups().map(headerGroup => (
                            <TableRow key={headerGroup.id}>
                                {headerGroup.headers.map(header => (
                                    <TableHead key={header.id}>
                                        {header.isPlaceholder
                                            ? null
                                            : flexRender(
                                                header.column.columnDef.header,
                                                header.getContext()
                                            )}
                                    </TableHead>
                                ))}
                            </TableRow>
                        ))}
                    </TableHeader>

                    <TableBody>
                        {table.getRowModel().rows.length ? (
                            table.getRowModel().rows.map(row => (
                                <TableRow key={row.id}>
                                    {row.getVisibleCells().map(cell => (
                                        <TableCell key={cell.id}>
                                            {flexRender(
                                                cell.column.columnDef.cell,
                                                cell.getContext()
                                            )}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell colSpan={columns.length}>
                                    Inga resultat
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>

            {/* PAGINATION */}
            {!isEmpty && (<div className="flex items-center justify-between">

                <div className="flex gap-2 items-center">
                    <span>Rows:</span>
                    <select
                        value={pageSize}
                        onChange={(e) => {
                            setPageSize(Number(e.target.value));
                            setPage(0);
                        }}
                        className="border px-2 py-1 rounded"
                    >
                        {[10, 20, 50].map(size => (
                            <option key={size} value={size}>
                                {size}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="flex gap-2 items-center">
                    <Button
                        onClick={() => setPage(p => Math.max(p - 1, 0))}
                        disabled={page === 0 || isEmpty}
                    >
                        Föregående
                    </Button>

                    <span>
        Sida {isEmpty ? 0 : page + 1} av {displayTotalPages}
    </span>

                    <Button
                        onClick={() => setPage(p => Math.min(p + 1, totalPages - 1))}
                        disabled={isEmpty || page + 1 >= totalPages}
                    >
                        Nästa
                    </Button>
                </div>

            </div>)}
        </div>
    );
}