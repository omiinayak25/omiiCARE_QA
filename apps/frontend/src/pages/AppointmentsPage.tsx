import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useForm } from 'react-hook-form';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import MenuItem from '@mui/material/MenuItem';
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
import {
  bookAppointment,
  fetchAppointments,
  fetchPatients,
  fetchProviders,
  type BookAppointmentInput,
} from '@/api/services';

interface BookFormValues {
  patientId: string;
  providerId: string;
  scheduledStart: string;
  scheduledEnd: string;
  reason: string;
}

export function AppointmentsPage() {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [dialogOpen, setDialogOpen] = useState(false);
  const [toast, setToast] = useState(false);
  const [conflict, setConflict] = useState(false);

  const appointmentsQuery = useQuery({
    queryKey: ['appointments', 0],
    queryFn: () => fetchAppointments(0),
  });
  const providersQuery = useQuery({ queryKey: ['providers'], queryFn: fetchProviders });
  const patientsQuery = useQuery({ queryKey: ['patients', '', 0], queryFn: () => fetchPatients('', 0) });

  const { register, handleSubmit, reset } = useForm<BookFormValues>({
    defaultValues: { patientId: '', providerId: '', scheduledStart: '', scheduledEnd: '', reason: '' },
  });

  const bookMutation = useMutation({
    mutationFn: (input: BookAppointmentInput) => bookAppointment(input),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['appointments'] });
      setDialogOpen(false);
      reset();
      setToast(true);
    },
    onError: (error) => {
      if (error instanceof AxiosError && error.response?.status === 422) {
        setConflict(true);
      }
    },
  });

  const onSubmit = handleSubmit((values) => {
    setConflict(false);
    bookMutation.mutate({
      patientId: Number(values.patientId),
      providerId: Number(values.providerId),
      scheduledStart: new Date(values.scheduledStart).toISOString(),
      scheduledEnd: new Date(values.scheduledEnd).toISOString(),
      reason: values.reason || undefined,
    });
  });

  const appointments = appointmentsQuery.data?.content ?? [];

  return (
    <Box data-testid="appointments-page">
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 2 }}>
        <Typography variant="h1">{t('appointments.title')}</Typography>
        <Button variant="contained" onClick={() => setDialogOpen(true)} data-testid="book-appointment">
          {t('appointments.book')}
        </Button>
      </Stack>

      <TableContainer component={Paper}>
        <Table size="small" data-testid="appointments-table">
          <TableHead>
            <TableRow>
              <TableCell>{t('appointments.patient')}</TableCell>
              <TableCell>{t('appointments.provider')}</TableCell>
              <TableCell>{t('appointments.start')}</TableCell>
              <TableCell>{t('appointments.end')}</TableCell>
              <TableCell>{t('appointments.status')}</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {appointments.map((appointment) => (
              <TableRow key={appointment.id} hover>
                <TableCell>#{appointment.patientId}</TableCell>
                <TableCell>#{appointment.providerId}</TableCell>
                <TableCell>{new Date(appointment.scheduledStart).toLocaleString()}</TableCell>
                <TableCell>{new Date(appointment.scheduledEnd).toLocaleString()}</TableCell>
                <TableCell>{appointment.status}</TableCell>
              </TableRow>
            ))}
            {appointmentsQuery.data && appointments.length === 0 && (
              <TableRow>
                <TableCell colSpan={5} align="center" data-testid="appointments-empty">
                  {t('appointments.empty')}
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>{t('appointments.book')}</DialogTitle>
        <form onSubmit={onSubmit}>
          <DialogContent>
            <Stack spacing={2} sx={{ mt: 1 }}>
              {conflict && (
                <Alert severity="warning" data-testid="appointment-conflict">
                  {t('appointments.conflict')}
                </Alert>
              )}
              <TextField label={t('appointments.patient')} select required defaultValue="" {...register('patientId', { required: true })}>
                {(patientsQuery.data?.content ?? []).map((patient) => (
                  <MenuItem key={patient.id} value={String(patient.id)}>
                    {patient.firstName} {patient.lastName} ({patient.mrn})
                  </MenuItem>
                ))}
              </TextField>
              <TextField label={t('appointments.provider')} select required defaultValue="" {...register('providerId', { required: true })}>
                {(providersQuery.data?.content ?? []).map((provider) => (
                  <MenuItem key={provider.id} value={String(provider.id)}>
                    {provider.firstName} {provider.lastName} — {provider.specialty}
                  </MenuItem>
                ))}
              </TextField>
              <TextField
                label={t('appointments.start')}
                type="datetime-local"
                required
                InputLabelProps={{ shrink: true }}
                inputProps={{ 'data-testid': 'appointment-start' }}
                {...register('scheduledStart', { required: true })}
              />
              <TextField
                label={t('appointments.end')}
                type="datetime-local"
                required
                InputLabelProps={{ shrink: true }}
                inputProps={{ 'data-testid': 'appointment-end' }}
                {...register('scheduledEnd', { required: true })}
              />
              <TextField label={t('appointments.reason')} {...register('reason')} />
            </Stack>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setDialogOpen(false)}>{t('patients.form.cancel')}</Button>
            <Button type="submit" variant="contained" disabled={bookMutation.isPending} data-testid="appointment-save">
              {t('patients.form.save')}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      <Snackbar open={toast} autoHideDuration={3000} onClose={() => setToast(false)} message={t('appointments.booked')} />
    </Box>
  );
}
