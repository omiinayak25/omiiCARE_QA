import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useForm } from 'react-hook-form';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import MenuItem from '@mui/material/MenuItem';
import Pagination from '@mui/material/Pagination';
import Paper from '@mui/material/Paper';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { createPatient, fetchPatients } from '@/api/services';
import type { CreatePatientInput } from '@/types';

const GENDERS = ['MALE', 'FEMALE', 'OTHER', 'UNKNOWN'];

export function PatientsPage() {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [toast, setToast] = useState(false);

  const patientsQuery = useQuery({
    queryKey: ['patients', search, page],
    queryFn: () => fetchPatients(search, page),
  });

  const { register, handleSubmit, reset, formState } = useForm<CreatePatientInput>({
    defaultValues: { firstName: '', lastName: '', dateOfBirth: '', gender: 'UNKNOWN' },
  });

  const createMutation = useMutation({
    mutationFn: createPatient,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['patients'] });
      setDialogOpen(false);
      reset();
      setToast(true);
    },
  });

  const onSubmit = handleSubmit((values) => createMutation.mutate(values));
  const data = patientsQuery.data;

  return (
    <Box data-testid="patients-page">
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 2 }}>
        <Typography variant="h1">{t('patients.title')}</Typography>
        <Button variant="contained" onClick={() => setDialogOpen(true)} data-testid="add-patient">
          {t('patients.add')}
        </Button>
      </Stack>

      <TextField
        fullWidth
        size="small"
        label={t('patients.search')}
        value={search}
        onChange={(event) => {
          setPage(0);
          setSearch(event.target.value);
        }}
        sx={{ mb: 2 }}
        inputProps={{ 'data-testid': 'patient-search' }}
      />

      {patientsQuery.isError && <Alert severity="error">{t('common.error')}</Alert>}

      <TableContainer component={Paper}>
        <Table size="small" data-testid="patients-table">
          <TableHead>
            <TableRow>
              <TableCell>{t('patients.mrn')}</TableCell>
              <TableCell>{t('patients.name')}</TableCell>
              <TableCell>{t('patients.dob')}</TableCell>
              <TableCell>{t('patients.gender')}</TableCell>
              <TableCell>{t('patients.status')}</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {(data?.content ?? []).map((patient) => (
              <TableRow key={patient.id} hover>
                <TableCell>{patient.mrn}</TableCell>
                <TableCell>
                  {patient.firstName} {patient.lastName}
                </TableCell>
                <TableCell>{patient.dateOfBirth}</TableCell>
                <TableCell>{patient.gender}</TableCell>
                <TableCell>{patient.status}</TableCell>
              </TableRow>
            ))}
            {data && data.content.length === 0 && (
              <TableRow>
                <TableCell colSpan={5} align="center" data-testid="patients-empty">
                  {t('patients.empty')}
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      {data && data.totalPages > 1 && (
        <Stack alignItems="center" sx={{ mt: 2 }}>
          <Pagination
            count={data.totalPages}
            page={data.page + 1}
            onChange={(_, value) => setPage(value - 1)}
          />
        </Stack>
      )}

      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>{t('patients.add')}</DialogTitle>
        <form onSubmit={onSubmit}>
          <DialogContent>
            <Stack spacing={2} sx={{ mt: 1 }}>
              {createMutation.isError && <Alert severity="error">{t('common.error')}</Alert>}
              <TextField
                label={t('patients.form.firstName')}
                required
                inputProps={{ 'data-testid': 'patient-firstName' }}
                {...register('firstName', { required: true })}
              />
              <TextField
                label={t('patients.form.lastName')}
                required
                inputProps={{ 'data-testid': 'patient-lastName' }}
                {...register('lastName', { required: true })}
              />
              <TextField
                label={t('patients.form.dateOfBirth')}
                type="date"
                required
                InputLabelProps={{ shrink: true }}
                inputProps={{ 'data-testid': 'patient-dob' }}
                {...register('dateOfBirth', { required: true })}
              />
              <TextField
                label={t('patients.form.gender')}
                select
                defaultValue="UNKNOWN"
                {...register('gender', { required: true })}
              >
                {GENDERS.map((gender) => (
                  <MenuItem key={gender} value={gender}>
                    {gender}
                  </MenuItem>
                ))}
              </TextField>
              <TextField label={t('patients.form.email')} type="email" {...register('email')} />
              <TextField label={t('patients.form.phone')} {...register('phone')} />
            </Stack>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setDialogOpen(false)}>{t('patients.form.cancel')}</Button>
            <Button
              type="submit"
              variant="contained"
              disabled={formState.isSubmitting || createMutation.isPending}
              data-testid="patient-save"
            >
              {t('patients.form.save')}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      <Snackbar
        open={toast}
        autoHideDuration={3000}
        onClose={() => setToast(false)}
        message={t('patients.created')}
      />
    </Box>
  );
}
